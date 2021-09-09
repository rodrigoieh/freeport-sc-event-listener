package nft.davinci.network

import io.vertx.pgclient.PgException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import nft.davinci.event.SmartContractEvent
import nft.davinci.network.config.ContractConfig
import nft.davinci.network.config.NetworkConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import nft.davinci.network.processor.EventProcessor
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.enterprise.inject.Instance

class ContractEventsListener(
    private val contractConfig: ContractConfig,
    private val networkConfig: NetworkConfig,
    private val lastScannedBlockRepository: LastScannedBlockRepository,
    private val covalentClient: CovalentClient,
    private val converters: Instance<ContractEventConverter<*>>,
    private val processorsMap: Map<String, EventProcessor<SmartContractEvent>>
) {
    private companion object {
        private const val OUT_OF_SYNC_THRESHOLD = 100
        private const val UNDEFINED_BLOCK = -1L
    }

    private val log = LoggerFactory.getLogger(javaClass)

    private val contract = contractConfig.address()
    private val lastScannedBlock = AtomicLong()
    private val isRunning = AtomicBoolean(true)
    private val isProcessing = AtomicBoolean(false)

    suspend fun init() = coroutineScope {
        log.info("Initializing Contract Event Listener for contract {}", contract)
        log.info("Updating last scanned block for contract {}", contract)

        val block = lastScannedBlockRepository.getLastScannedBlock(contractConfig)
        lastScannedBlock.set(block)

        log.info("Contract Event Listener for contract {} initialized with last scanned block {}", contract, block)
        log.info("Starting Contract Event Listener consumer for contract {}", contract)

        isRunning.set(true)
        listen()
    }

    private suspend fun listen() = coroutineScope {
        while (isRunning.get()) {
            isProcessing.set(true)
            runCatching {
                val latestBlockFromNetwork = getLatestBlockFromNetwork()
                if (latestBlockFromNetwork != UNDEFINED_BLOCK) {
                    if (latestBlockFromNetwork - lastScannedBlock.get() > OUT_OF_SYNC_THRESHOLD) {
                        log.info("Event scanner for contract $contract out of sync. Syncing events from block ${lastScannedBlock.get()} to $latestBlockFromNetwork")
                        syncBatch(latestBlockFromNetwork)
                    } else {
                        sync(latestBlockFromNetwork)
                    }
                }
            }.onFailure {
                log.error("Error on consuming events for contract {}", contract, it)
            }
            isProcessing.set(false)
            delay(networkConfig.pollInterval().toMillis())
        }
        log.info("Contract Event Listener consumer for contract {} was stopped", contract)
    }

    private suspend fun syncBatch(toBlock: Long) = coroutineScope {
        var endingBlock = lastScannedBlock.get() + OUT_OF_SYNC_THRESHOLD
        while (endingBlock <= toBlock) {
            if (!sync(endingBlock)) {
                return@coroutineScope
            }
            endingBlock += OUT_OF_SYNC_THRESHOLD
        }
        log.info("Event scanner for contract {} synced with network", contract)
    }

    private suspend fun sync(toBlock: Long): Boolean = coroutineScope {
        val startingBlock = lastScannedBlock.get()
        val rs = covalentClient.getContractEvents(
            networkConfig.chainId(),
            contract,
            startingBlock,
            toBlock,
            networkConfig.covalentApiKey()
        )
        if (rs.error) {
            log.error(
                "Unable to retrieve events from block {} to block {} for contract {}. {}",
                startingBlock,
                toBlock,
                contract,
                rs.errorMessage
            )
            log.warn("Skipping consuming until next successful request")
            return@coroutineScope false
        }
        rs.data!!.items.forEach {
            convertAndProcess(it)
            updateLastScannedBlockNumber(it.blockHeight)
        }
        updateLastScannedBlockNumber(toBlock)
        true
    }

    private suspend fun convertAndProcess(event: ContractEvent) = coroutineScope {
        val converter = converters.firstOrNull { it.canConvert(event) }
        if (converter == null) {
            log.info("Unable to find converter for event at tx {} for contract {}", event.txHash, contract)
            return@coroutineScope
        }
        log.info("Converting event at tx {}", event.txHash)
        val converted = converter.convert(event)
        val convertedType = converted::class.java.simpleName
        log.info("Converted event type is {}", convertedType)
        val processor = processorsMap[convertedType]
        if (processor == null) {
            log.warn("Unable to find processor for event type {} for contract {}. Skip.", convertedType, contract)
            return@coroutineScope
        }
        runCatching {
            processor.process(converted)
        }.onFailure {
            if (it is PgException && it.code == "23505") {
                log.warn("Duplicate event {} in contract {}", converted, contract, it)
            } else {
                throw it
            }
        }
    }

    private suspend fun updateLastScannedBlockNumber(blockNumber: Long) = coroutineScope {
        lastScannedBlockRepository.updateLastScannedBlockNumber(contract, blockNumber)
        lastScannedBlock.set(blockNumber)
    }

    private suspend fun getLatestBlockFromNetwork(): Long = coroutineScope {
        val rs = covalentClient.getLatestBlock(networkConfig.chainId(), networkConfig.covalentApiKey())
        if (rs.error) {
            log.error("Unable to get last block from network. {}", rs.errorMessage)
            log.warn("Skipping consuming until next successful request")
            UNDEFINED_BLOCK
        }
        rs.data!!.items[0].height
    }

    suspend fun reset() {
        log.info("Reset Contract Event Listener for contract {}", contract)
        isRunning.set(false)
        while (isProcessing.get()) {
            log.info("Waiting for current events to be processed for contract {}", contract)
            delay(500)
        }
    }

    fun stop() {
        log.info("Stopping Contract Event Listener for contract {}", contract)
        isRunning.set(false)
    }
}
