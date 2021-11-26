package nft.freeport.listener

import io.smallrye.mutiny.Multi
import io.smallrye.reactive.messaging.annotations.Broadcast
import nft.freeport.DDC_PROCESSOR_ID
import nft.freeport.NO_EVENTS_BLOCK_OFFSET
import nft.freeport.SMART_CONTRACT_EVENTS_DDC_TOPIC_NAME
import nft.freeport.covalent.CovalentClient
import nft.freeport.covalent.config.NetworkConfig
import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.covalent.dto.CovalentResponse
import nft.freeport.listener.config.ContractsConfig
import nft.freeport.listener.event.BlockProcessedEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventConverter
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.processorsPosition.ProcessorsPositionManager
import nft.freeport.listener.processorsPosition.dto.ProcessedEventPosition
import nft.freeport.listener.processorsPosition.dto.ProcessingBlockState.DONE
import nft.freeport.processor.ddc.DdcProcessor
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import kotlin.streams.asStream

/**
 * It reads event from covalent and broadcast them to in memory channels.
 */
@ApplicationScoped
class SmartContractEventsReader(
    private val networkConfig: NetworkConfig,
    @RestClient private val covalentClient: CovalentClient,
    private val converter: SmartContractEventConverter,
    private val stateProvider: ProcessorsPositionManager,

    contractsConfig: ContractsConfig,
) {
    private companion object {
        /**
         * For decoded log events and other endpoints where you are asked to specify a block range, you are limited to a million block range after which point you need to make a follow-up call using the pagination info.
         */
        private const val COVALENT_BLOCKS_LIMIT = 1_000_000
        private const val COVALENT_EVENTS_LIMIT = 100
        private const val UNDEFINED_BLOCK = -1L
    }

    private val log = LoggerFactory.getLogger(javaClass)

    private val contracts = contractsConfig.contracts().values.map { it.address() }

    /**
     * Read events from the blockchain and stream them to [DdcProcessor]
     */
    @Outgoing(SMART_CONTRACT_EVENTS_DDC_TOPIC_NAME)
    @Broadcast
    fun ddcStream(): Multi<SmartContractEventData<out SmartContractEvent>> {
        return Multi.createFrom()
            .ticks().every(networkConfig.pollInterval())
            .flatMap {
                contracts
                    .map {
                        // TODO for others get the most outdated position (others == all processors without ddc)
                        val ddcPosition = stateProvider.getCurrentPosition(DDC_PROCESSOR_ID, it)
                        val nextEvents = readEvents(it, ddcPosition)

                        nextEvents ?: Multi.createFrom().empty()
                    }
                    .fold(Multi.createFrom().empty()) { a, b -> Multi.createBy().merging().streams(a, b) }
            }
    }

    // todo remove, just for debug
    @Incoming(SMART_CONTRACT_EVENTS_DDC_TOPIC_NAME)
    @Transactional
    fun testConsume(event: SmartContractEventData<out SmartContractEvent>) {
        println("processed: ${event.rawEvent.blockHeight} ${event.rawEvent.logOffset} ${event.event::class}")
    }

    /**
     * Reads events for contract after [position]
     */
    fun readEvents(
        contract: String,
        // todo to think is it okay, it's the lowest position in pool, so we have to take care about offset block and state also
        position: ProcessedEventPosition
    ): Multi<SmartContractEventData<out SmartContractEvent>>? {
        return runCatching {
            val latestBlockFromNetwork = getLatestBlockFromNetwork()

            when {
                latestBlockFromNetwork == UNDEFINED_BLOCK -> return null
                // all processors are handled this block, just skip
                position.block == latestBlockFromNetwork && position.currentState == DONE -> return null
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
                readBatch(contract, fromBlock = fromBlock, toBlock = latestBlockFromNetwork)
            } else {
                read(contract, fromBlock = fromBlock, toBlock = latestBlockFromNetwork)
            }
        }.onFailure {
            log.error("Error on consuming events for contract {}", contract, it)
        }.getOrNull()
    }

    /** TODO check if it works after my changes, refactor (remove state)
     * @author vadim.hleif
     */
    private fun readBatch(
        contract: String,
        fromBlock: Long,
        toBlock: Long
    ): Multi<SmartContractEventData<out SmartContractEvent>> {
        var endingBlock = fromBlock + COVALENT_BLOCKS_LIMIT

        var result: Multi<SmartContractEventData<out SmartContractEvent>> = Multi.createFrom().empty()
        while (endingBlock <= toBlock) {
            var multi = read(contract, fromBlock = fromBlock, toBlock = endingBlock)

            while (multi != null) {
                result = Multi.createBy().merging().streams(result, multi)
                multi = read(contract, endingBlock, toBlock)
            }

            endingBlock += COVALENT_BLOCKS_LIMIT
        }
        log.info("Event scanner for contract {} synced with network", contract)

        return result
    }

    private fun read(
        contract: String,
        fromBlock: Long,
        toBlock: Long
    ): Multi<SmartContractEventData<out SmartContractEvent>>? {
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
            return null
        }

        val events: List<ContractEvent> = requireNotNull(rs.data).items
        val numberOfEvents = events.size
        // Covalent doesn't return more than 100 events per 1 API call
        // we need to decrease block limit
        if (numberOfEvents == COVALENT_EVENTS_LIMIT) {
            val half = (toBlock - fromBlock) / 2

            return Multi.createBy().merging()
                .streams(
                    read(contract, fromBlock = fromBlock, toBlock = half),
                    read(contract, fromBlock = half, toBlock = toBlock)
                )
        }

        return Multi.createFrom().items(
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
                                // todo to think what to do with other fields, maybe covalent returns them(time hash)? anyway it's not important thing,
                                //  because only block, offset and contract fields are used now
                                txHash = "0x0", blockSignedAt = Instant.MIN.toString(),
                                rawLogTopics = emptyList(), rawLogData = null, decoded = null,
                            )
                        )
                    )
                }
                .asStream()
        )
    }

    @Transactional
    internal fun convertEvent(
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