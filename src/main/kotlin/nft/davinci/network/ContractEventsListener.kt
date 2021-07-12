package nft.davinci.network

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import io.vertx.pgclient.PgException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nft.davinci.event.SmartContractEvent
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import nft.davinci.network.processor.EventProcessor
import nft.davinci.reset.CleanUp
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.server.runtime.kotlin.ApplicationCoroutineScope
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.Instance

@ApplicationScoped
class ContractEventsListener(
    private val networkConfig: NetworkConfig,
    private val lastScannedBlockRepository: LastScannedBlockRepository,
    private val applicationCoroutineScope: ApplicationCoroutineScope,
    @RestClient private val covalentClient: CovalentClient,
    private val converters: Instance<ContractEventConverter<*>>,
    private val processorsMap: Map<String, EventProcessor<SmartContractEvent>>,
    private val cleanUp: CleanUp
) {
    private companion object {
        private const val OUT_OF_SYNC_THRESHOLD = 100
        private const val UNDEFINED_BLOCK = -1L
    }

    private val log = LoggerFactory.getLogger(javaClass)

    private val lastScannedBlock = AtomicLong()
    private val isRunning = AtomicBoolean(true)
    private val isProcessing = AtomicBoolean(false)

    fun scheduleInit(@Observes e: StartupEvent) {
        applicationCoroutineScope.launch { init() }
    }

    private suspend fun init() = coroutineScope {
        log.info("Initializing Contract Event Listener")
        log.info("Updating last scanned block")

        val block = lastScannedBlockRepository.getLastScannedBlock()
        lastScannedBlock.set(block)

        log.info("Contract Event Listener initialized with last scanned block {}", block)
        log.info("Starting Contract Event Listener consumer")
        listen()
    }

    private suspend fun listen() = coroutineScope {
        while (isRunning.get()) {
            isProcessing.set(true)
            runCatching {
                val latestBlockFromNetwork = getLatestBlockFromNetwork()
                if (latestBlockFromNetwork != UNDEFINED_BLOCK) {
                    if (latestBlockFromNetwork - lastScannedBlock.get() > OUT_OF_SYNC_THRESHOLD) {
                        log.info("Event scanner out of sync. Syncing events from block ${lastScannedBlock.get()} to $latestBlockFromNetwork")
                        syncBatch(latestBlockFromNetwork)
                    } else {
                        sync(latestBlockFromNetwork)
                    }
                }
            }.onFailure {
                log.error("Error on consuming events", it)
            }
            isProcessing.set(false)
            delay(networkConfig.pollInterval().toMillis())
        }
        log.info("Contract Event Listener consumer was stopped")
    }

    private suspend fun syncBatch(toBlock: Long) = coroutineScope {
        var endingBlock = lastScannedBlock.get() + OUT_OF_SYNC_THRESHOLD
        while (endingBlock <= toBlock) {
            if (!sync(endingBlock)) {
                return@coroutineScope
            }
            endingBlock += OUT_OF_SYNC_THRESHOLD
        }
        log.info("Event scanner synced with network")
    }

    private suspend fun sync(toBlock: Long): Boolean = coroutineScope {
        val startingBlock = lastScannedBlock.get()
        val rs = covalentClient.getContractEvents(
            networkConfig.chainId(),
            networkConfig.contractAddress(),
            startingBlock,
            toBlock,
            networkConfig.covalentApiKey()
        )
        if (rs.error) {
            log.error(
                "Unable to retrieve events from block {} to block {}. {}",
                startingBlock,
                toBlock,
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
            log.info("Unable to find converter for event at tx {}", event.txHash)
            return@coroutineScope
        }
        log.info("Converting event at tx {}", event.txHash)
        val converted = converter.convert(event)
        val convertedType = converted::class.java.simpleName
        log.info("Converted event type is {}", convertedType)
        val processor = processorsMap[convertedType]
        if (processor == null) {
            log.warn("Unable to find processor for event type {}. Skip.", convertedType)
            return@coroutineScope
        }
        runCatching {
            processor.process(converted)
        }.onFailure {
            if (it is PgException && it.code == "23505") {
                log.warn("Duplicate event {}", converted, it)
            } else {
                throw it
            }
        }
    }

    private suspend fun updateLastScannedBlockNumber(blockNumber: Long) = coroutineScope {
        lastScannedBlockRepository.updateLastScannedBlockNumber(blockNumber)
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
        log.info("Reset Contract Event Listener")
        isRunning.set(false)
        while (isProcessing.get()) {
            log.info("Waiting for current events to be processed")
            delay(500)
        }
        cleanUp.truncateDb()
        isRunning.set(true)
        applicationCoroutineScope.launch { init() }
    }

    fun onStop(@Observes ev: ShutdownEvent) {
        log.info("Stopping Contract Event Listener")
        isRunning.set(false)
    }
}
