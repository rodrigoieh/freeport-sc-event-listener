package nft.freeport.network

import io.quarkus.runtime.ShutdownEvent
import nft.freeport.event.SmartContractEvent
import nft.freeport.network.block.LastScannedBlockEntity
import nft.freeport.network.config.ContractConfig
import nft.freeport.network.config.NetworkConfig
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.dto.ContractEvent
import nft.freeport.network.processor.EventProcessor
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.Instance
import javax.transaction.Transactional

/**
 * On application start - read last scanned block for each contract from database
 * or uses start block from config.
 *
 * Schedules periodic task that reads all events from last scanned block to the current block.
 * Each event is converted and processed if Converter and Processor implementation exists, skipped otherwise.
 *
 * To make sync process faster, we are trying to read 1 000 000 blocks per each call - this is a current limit
 * of Covalent API which is used to retrieve events information. However, API also have a limit of 100 events
 * per response. That is why we have a check if we have 100 events in response and if so, split current
 * synchronisation call to two recursive calls by dividing current number of scanning blocks by two.
 *
 * On application stop tries to wait current jobs until they finish processing for graceful shutdown.
 */
@ApplicationScoped
class ContractsEventsListener(
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

    private val lastScannedBlocks = ConcurrentHashMap<String, Long>()

    fun init(cfg: ContractConfig) {
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
        log.info(
            "Contract Event Listener for contract {} initialized with last scanned block {}",
            contract,
            lastScannedBlock
        )
        log.info("Starting Contract Event Listener consumer for contract {}", contract)
    }

    /**
     * It helps to prevent parallel running of sync jobs.
     * [ContractsEventListenerScheduler] doesn't trigger [sync] method multiple times, but we don't have guarantees that all jobs will be completed by the time of the next trigger.
     */
    private val syncingContracts = ConcurrentHashMap.newKeySet<String>()
    private val shuttingDown = AtomicBoolean(false)

    fun sync(contract: String) {
        // skip if shutdown is enabled
        if (shuttingDown.get()) return

        // skip contract which is syncing
        if (!syncingContracts.add(contract)) {
            log.warn("attempt to sync contract: $contract, but sync is already in progress.")
            return
        }

        runCatching {
            val latestBlockFromNetwork = getLatestBlockFromNetwork()
            if (latestBlockFromNetwork != UNDEFINED_BLOCK) {
                val lastScannedBlock = lastScannedBlocks.getValue(contract)

                // skip already actual contract
                if (lastScannedBlock == latestBlockFromNetwork) return@runCatching
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

        syncingContracts.remove(contract)
    }

    fun onStop(@Observes ev: ShutdownEvent) {
        shuttingDown.set(true)
        while (syncingContracts.isNotEmpty()) {
            log.info("Waiting for listeners to stop")
            Thread.sleep(500)
        }
        log.info("All listeners stopped")
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
    private fun updateLastScannedBlockNumber(contract: String, blockNumber: Long) {
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

}