package nft.freeport.listener

import io.quarkus.scheduler.Scheduled
import nft.freeport.CMS_PROCESSOR_ID
import nft.freeport.DDC_PROCESSOR_ID
import nft.freeport.FREEPORT_PROCESSOR_ID
import nft.freeport.NO_EVENTS_BLOCK_OFFSET
import nft.freeport.covalent.CovalentClient
import nft.freeport.covalent.config.NetworkConfig
import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.covalent.dto.CovalentResponse
import nft.freeport.listener.config.ContractsConfig
import nft.freeport.listener.event.BlockProcessedEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventConverter
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.listener.position.dto.ProcessingBlockState.DONE
import nft.freeport.listener.skip.SkipCms
import nft.freeport.listener.skip.SkipDdc
import nft.freeport.listener.skip.SkipFreeport
import nft.freeport.processor.EventProcessor
import nft.freeport.processor.cms.CmsEventProcessorBase
import nft.freeport.processor.ddc.DdcProcessor
import nft.freeport.processor.freeport.FreeportEventProcessorBase
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SmartContractEventsReader(
    private val networkConfig: NetworkConfig,
    @RestClient private val covalentClient: CovalentClient,
    private val converter: SmartContractEventConverter,
    private val positionManager: ProcessorsPositionManager,

    private val freeportEventProcessor: FreeportEventProcessorBase,
    private val ddcEventProcessor: DdcProcessor,
    private val cmsEventProcessor: CmsEventProcessorBase,

    contractsConfig: ContractsConfig,
) {
    private companion object {
        /**
         * For decoded log events and other endpoints where you are asked to specify a block range, you are limited to
         * a million block range after which point you need to make a follow-up call using the pagination info.
         */
        private const val COVALENT_BLOCKS_LIMIT = 1_000_000
        private const val COVALENT_EVENTS_LIMIT = 100
        private const val UNDEFINED_BLOCK = -1L
    }

    private val log = LoggerFactory.getLogger(javaClass)

    private val contracts = contractsConfig.contracts().values.map { it.address() }

    @Scheduled(
        every = "{network.poll-interval}",
        concurrentExecution = Scheduled.ConcurrentExecution.SKIP,
        skipExecutionIf = SkipFreeport::class
    )
    fun freeportProcessor() {
        readAndProcess(freeportEventProcessor, FREEPORT_PROCESSOR_ID)
    }

    @Scheduled(
        every = "{network.poll-interval}",
        concurrentExecution = Scheduled.ConcurrentExecution.SKIP,
        skipExecutionIf = SkipDdc::class
    )
    fun ddcProcessor() {
        readAndProcess(ddcEventProcessor, DDC_PROCESSOR_ID)
    }

    @Scheduled(
        every = "{network.poll-interval}",
        concurrentExecution = Scheduled.ConcurrentExecution.SKIP,
        skipExecutionIf = SkipCms::class
    )
    fun cmsProcessor() {
        readAndProcess(cmsEventProcessor, CMS_PROCESSOR_ID)
    }

    private fun readAndProcess(processor: EventProcessor, processorId: String) {
        contracts.forEach { readAndProcess(it, processor, processorId) }
    }

    private fun readAndProcess(contract: String, processor: EventProcessor, processorId: String) {
        val position = positionManager.getCurrentPosition(processorId, contract)
        val latestBlockFromNetwork = getLatestBlockFromNetwork()
        if (latestBlockFromNetwork == UNDEFINED_BLOCK ||
            // all processors are handled this block, just skip
            (position.block == latestBlockFromNetwork && position.currentState == DONE)
        ) {
            return
        }
        // take next block if the current one was processed to prevent extra data fetching
        val fromBlock = if (position.currentState == DONE) position.block + 1 else position.block
        if (latestBlockFromNetwork - fromBlock > COVALENT_BLOCKS_LIMIT) {
            log.info(
                "Event scanner for contract {} out of sync. Syncing events from block {} offset {} to {}",
                contract,
                fromBlock,
                position.offset,
                latestBlockFromNetwork,
            )
            readAndProcessBatch(contract, processor, fromBlock, latestBlockFromNetwork)
        } else {
            readAndProcess(contract, processor, fromBlock, latestBlockFromNetwork)
        }
    }

    private fun readAndProcessBatch(
        contract: String,
        processor: EventProcessor,
        fromBlock: Long,
        toBlock: Long
    ) {
        var from = fromBlock
        var to = fromBlock + COVALENT_BLOCKS_LIMIT
        while (to <= toBlock) {
            if (!readAndProcess(contract, processor, fromBlock = from, toBlock = to)) {
                return
            }
            from = to + 1
            to += COVALENT_BLOCKS_LIMIT
        }
    }

    private fun readAndProcess(
        contract: String,
        processor: EventProcessor,
        fromBlock: Long,
        toBlock: Long
    ): Boolean {
        val rs: CovalentResponse<ContractEvent> = covalentClient.getContractEvents(
            chainId = networkConfig.chainId(),
            contractAddress = contract,
            startingBlock = fromBlock,
            endingBlock = toBlock,
            apiKey = networkConfig.covalentApiKey()
        )

        if (rs.error) {
            log.error(
                "Unable to retrieve events from block {} to block {} for contract {}. {}",
                fromBlock,
                toBlock,
                contract,
                rs.errorMessage,
            )
            log.warn("Skipping consuming until next successful request")
            return false
        }

        val events: List<ContractEvent> = requireNotNull(rs.data).items
        val numberOfEvents = events.size
        // Covalent doesn't return more than 100 events per 1 API call
        // we need to decrease block limit
        if (numberOfEvents == COVALENT_EVENTS_LIMIT) {
            val half = (toBlock - fromBlock) / 2
            val middleBlock = fromBlock + half
            return readAndProcess(contract, processor, fromBlock, middleBlock)
                    // +1 to prevent requesting the same block twice
                    && readAndProcess(contract, processor, middleBlock + 1, toBlock)
        }

        events
            .groupBy { it.blockHeight }
            .asSequence()
            .flatMap { (block, events) ->
                val lastEvent = events.last()

                events.asSequence().mapNotNull { convertEvent(contract, event = it) } + SmartContractEventData(
                    contract = contract,
                    event = BlockProcessedEvent,
                    rawEvent = ContractEvent(
                        blockSignedAt = lastEvent.blockSignedAt,
                        blockHeight = block, txHash = lastEvent.txHash, logOffset = lastEvent.logOffset,
                        rawLogTopics = emptyList(), rawLogData = null, decoded = null,
                    )
                )
            }
            .ifEmpty {
                sequenceOf(
                    SmartContractEventData(
                        contract = contract,
                        event = BlockProcessedEvent,
                        rawEvent = ContractEvent(
                            blockHeight = toBlock, logOffset = NO_EVENTS_BLOCK_OFFSET,
                            txHash = "0x0", blockSignedAt = Instant.MIN.toString(),
                            rawLogTopics = emptyList(), rawLogData = null, decoded = null,
                        )
                    )
                )
            }
            .forEach(processor::processAndCommit)
        return true
    }

    private fun convertEvent(
        contract: String,
        event: ContractEvent
    ): SmartContractEventData<out SmartContractEvent>? {
        val converted: SmartContractEvent = converter.convert(event) ?: return null
        log.info("Converting event at tx {}", event.txHash)

        val convertedType = converted::class.java.simpleName
        log.info("Converted event type is {}", convertedType)

        return SmartContractEventData(contract = contract, event = converted, rawEvent = event)
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