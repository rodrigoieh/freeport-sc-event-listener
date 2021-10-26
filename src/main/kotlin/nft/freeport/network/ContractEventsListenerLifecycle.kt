package nft.freeport.network

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import nft.freeport.event.SmartContractEvent
import nft.freeport.network.block.LastScannedBlockEntity
import nft.freeport.network.config.ContractConfig
import nft.freeport.network.config.ContractsConfig
import nft.freeport.network.config.NetworkConfig
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.dto.ContractEvent
import nft.freeport.network.processor.EventProcessor
import org.eclipse.microprofile.context.ManagedExecutor
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.Instance
import javax.transaction.Transactional

@ApplicationScoped
class ContractEventsListenerLifecycle(
    contractsConfig: ContractsConfig,
    private val executor: ManagedExecutor,
    private val networkConfig: NetworkConfig,
    @RestClient private val covalentClient: CovalentClient,
    private val converters: Instance<ContractEventConverter<*>>,
    private val processorsMap: Map<String, EventProcessor<SmartContractEvent>>
) {
    private companion object {
        private const val COVALENT_BLOCKS_LIMIT = 1_000_000
        private const val COVALENT_EVENTS_LIMIT = 100
        private const val UNDEFINED_BLOCK = -1L
    }

    private val log = LoggerFactory.getLogger(javaClass)

    private val contracts = contractsConfig.contracts().values
    private val lastScannedBlocks = mutableMapOf<String, Long>()
    private val runningStatuses = mutableMapOf<String, Boolean>()
    private val shuttingDown = AtomicBoolean(false)

    @Transactional
    fun onStart(@Observes e: StartupEvent) {
        contracts.forEach(::init)
    }

    private fun init(cfg: ContractConfig) {
        val contract = cfg.address()
        log.info("Initializing Contract Event Listener for contract {}", contract)
        log.info("Updating last scanned block for contract {}", contract)
        val block = LastScannedBlockEntity.findById(contract)?.blockHeight
        val lastScannedBlock = if (block != null) {
            block
        } else {
            LastScannedBlockEntity(contract, cfg.firstBlockNumber()).persist()
            cfg.firstBlockNumber()
        }
        lastScannedBlocks[contract] = lastScannedBlock
        log.info("Contract Event Listener for contract {} initialized with last scanned block {}", contract, lastScannedBlock)
        log.info("Starting Contract Event Listener consumer for contract {}", contract)
    }

    @Scheduled(every = "{network.poll-interval}", skipExecutionIf = TestModeEnabled::class)
    fun sync() {
        contracts.map(ContractConfig::address)
            .forEach { contract -> executor.execute { sync(contract) } }
    }

    private fun sync(contract: String) {
        if (runningStatuses.getOrDefault(contract, false)) {
            // skip contract which is syncing
            return
        }
        runningStatuses[contract] = true
        runCatching {
            val latestBlockFromNetwork = getLatestBlockFromNetwork()
            if (latestBlockFromNetwork != UNDEFINED_BLOCK) {
                val lastScannedBlock = lastScannedBlocks.getValue(contract)
                if (latestBlockFromNetwork - lastScannedBlock > COVALENT_BLOCKS_LIMIT) {
                    log.info("Event scanner for contract $contract out of sync. Syncing events from block $lastScannedBlock to $latestBlockFromNetwork")
                    syncBatch(contract, latestBlockFromNetwork)
                } else {
                    sync(contract, latestBlockFromNetwork)
                }
            }
        }.onFailure {
            log.error("Error on consuming events for contract {}", contract, it)
        }
        runningStatuses[contract] = false
    }

    private fun syncBatch(contract: String, toBlock: Long) {
        var endingBlock = lastScannedBlocks.getValue(contract) + COVALENT_BLOCKS_LIMIT
        while (endingBlock <= toBlock) {
            if (!sync(contract, endingBlock)) {
                return
            }
            endingBlock += COVALENT_BLOCKS_LIMIT
        }
        log.info("Event scanner for contract {} synced with network", contract)
    }

    private fun sync(contract: String, toBlock: Long): Boolean {
        if (shuttingDown.get()) {
            return false
        }
        val startingBlock = lastScannedBlocks.getValue(contract)
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
            return false
        }
        val items = requireNotNull(rs.data).items
        val numberOfEvents = items.size
        // Covalent doesn't return more than 100 events per 1 API call
        // we need to decrease block limit
        if (numberOfEvents == COVALENT_EVENTS_LIMIT) {
            val half = (toBlock - startingBlock) / 2
            return sync(contract, startingBlock + half) && sync(contract, toBlock)
        }
        items.forEach {
            convertAndProcess(contract, it)
            updateLastScannedBlockNumber(contract, it.blockHeight)
        }
        updateLastScannedBlockNumber(contract, toBlock)
        return true
    }

    private fun convertAndProcess(contract: String, event: ContractEvent) {
        val converter = converters.firstOrNull { it.canConvert(event) }
        if (converter == null) {
            log.info("Unable to find converter for event at tx {} for contract {}", event.txHash, contract)
            return
        }
        log.info("Converting event at tx {}", event.txHash)
        val converted = converter.convert(event)
        val convertedType = converted::class.java.simpleName
        log.info("Converted event type is {}", convertedType)
        val processor = processorsMap[convertedType]
        if (processor == null) {
            log.warn("Unable to find processor for event type {} for contract {}. Skip.", convertedType, contract)
            return
        }
        processor.process(converted)
    }

    @Transactional
    internal fun updateLastScannedBlockNumber(contract: String, blockNumber: Long) {
        LastScannedBlockEntity.findById(contract)?.apply {
            blockHeight = blockNumber
            persist()
        }
        lastScannedBlocks[contract] = blockNumber
    }

    private fun getLatestBlockFromNetwork(): Long {
        val rs = covalentClient.getLatestBlock(networkConfig.chainId(), networkConfig.covalentApiKey())
        if (rs.error) {
            log.error("Unable to get last block from network. {}", rs.errorMessage)
            log.warn("Skipping consuming until next successful request")
            return UNDEFINED_BLOCK
        }
        return requireNotNull(rs.data).items[0].height
    }

    fun onStop(@Observes ev: ShutdownEvent) {
        shuttingDown.set(true)
        while (runningStatuses.any { it.value }) {
            log.info("Waiting for listeners to stop")
            Thread.sleep(500)
        }
        log.info("All listeners stopped")
    }
}