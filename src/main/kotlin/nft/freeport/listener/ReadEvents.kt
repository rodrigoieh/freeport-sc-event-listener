package nft.freeport.listener

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import io.vertx.core.eventbus.EventBus
import nft.freeport.SMART_CONTRACT_EVENTS_TOPIC_NAME
import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.SmartContractEventConverter
import nft.freeport.covalent.CovalentClient
import nft.freeport.listener.config.ContractConfig
import nft.freeport.listener.config.ContractsConfig
import nft.freeport.covalent.config.NetworkConfig
import nft.freeport.covalent.dto.ContractEvent
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.transaction.Transactional

@ApplicationScoped
class ReadEvents(
    contractsConfig: ContractsConfig,
    private val networkConfig: NetworkConfig,
    @RestClient private val covalentClient: CovalentClient,
    private val converter: SmartContractEventConverter,
    private val objectMapper: ObjectMapper,
    private val bus: EventBus
) : Runnable {
    private companion object {
        private const val COVALENT_BLOCKS_LIMIT = 1_000_000
        private const val COVALENT_EVENTS_LIMIT = 100
        private const val UNDEFINED_BLOCK = -1L
    }

    private val log = LoggerFactory.getLogger(javaClass)
    private val contracts = contractsConfig.contracts().values
    private val lastScannedBlocks = mutableMapOf<String, Long>()

    @Transactional
    fun init(@Observes event: StartupEvent) {
        contracts.forEach(::initLastScannedBlock)
    }

    private fun initLastScannedBlock(cfg: ContractConfig) {
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
    }

    /**
     * Read events from the blockchain, store them in database and send them to the workers.
     */
    @Scheduled(every = "{network.poll-interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    override fun run() {
        contracts.forEach(::readEvents)
    }

    private fun readEvents(cfg: ContractConfig) {
        val contract = cfg.address()
        runCatching {
            val latestBlockFromNetwork = getLatestBlockFromNetwork()
            val lastScannedBlock = lastScannedBlocks.getValue(contract)
            if (latestBlockFromNetwork == UNDEFINED_BLOCK || lastScannedBlock == latestBlockFromNetwork) {
                return
            }
            if (latestBlockFromNetwork - lastScannedBlock > COVALENT_BLOCKS_LIMIT) {
                log.info(
                    "Event scanner for contract {} out of sync. Syncing events from block {} to {}",
                    contract,
                    lastScannedBlock,
                    latestBlockFromNetwork
                )
                readBatch(contract, latestBlockFromNetwork)
            } else {
                read(contract, latestBlockFromNetwork)
            }
        }.onFailure {
            log.error("Error on consuming events for contract {}", contract, it)
        }
    }

    private fun readBatch(contract: String, toBlock: Long) {
        var endingBlock = lastScannedBlocks.getValue(contract) + COVALENT_BLOCKS_LIMIT
        while (endingBlock <= toBlock) {
            if (!read(contract, endingBlock)) {
                return
            }
            endingBlock += COVALENT_BLOCKS_LIMIT
        }
        log.info("Event scanner for contract {} synced with network", contract)
    }

    private fun read(contract: String, toBlock: Long): Boolean {
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
        val events = requireNotNull(rs.data).items
        val numberOfEvents = events.size
        // Covalent doesn't return more than 100 events per 1 API call
        // we need to decrease block limit
        if (numberOfEvents == COVALENT_EVENTS_LIMIT) {
            val half = (toBlock - startingBlock) / 2
            return read(contract, startingBlock + half) && read(contract, toBlock)
        }
        events.forEach {
            processEvent(contract, it)
                ?.also { bus.publish(SMART_CONTRACT_EVENTS_TOPIC_NAME, it) }
        }
        updateLastScannedBlockNumber(contract, toBlock)
        return true
    }

    @Transactional
    internal fun processEvent(contract: String, event: ContractEvent): SmartContractEventEntity? {
        val converted = converter.convert(event)
        var entity: SmartContractEventEntity? = null
        if (converted != null) {
            log.info("Converting event at tx {}", event.txHash)
            val convertedType = converted::class.java.simpleName
            log.info("Converted event type is {}", convertedType)
            entity = SmartContractEventEntity(
                id = null,
                name = convertedType,
                payload = objectMapper.writeValueAsString(converted),
                timestamp = Instant.parse(event.blockSignedAt),
                txHash = event.txHash
            )
            entity.persist()
        }
        updateLastScannedBlockNumber(contract, event.blockHeight)
        return entity
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
}