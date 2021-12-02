package nft.freeport.listener.position

import nft.freeport.listener.config.ContractsConfig
import nft.freeport.listener.position.dto.ProcessedEventPosition
import nft.freeport.listener.position.dto.ProcessingBlockState.NEW
import nft.freeport.listener.position.entity.ProcessorLastScannedEventPositionEntity
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import kotlin.streams.asSequence

@ApplicationScoped
class ProcessorsPositionManager(contractsConfig: ContractsConfig) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** it's lateinit to fail on access before [initLastScannedPositions] is called **/
    private lateinit var lastPositionByProcessorAndContract: MutableMap<PositionKey, ProcessedEventPosition>

    /** Fetch the latest state from the database **/
    @Transactional
    @PostConstruct
    internal fun initLastScannedPositions() {
        lastPositionByProcessorAndContract = ProcessorLastScannedEventPositionEntity.findAll().stream().asSequence()
            .onEach { positionEntity -> log.info("The actual processor position: {}", positionEntity) }
            .map { it.toKey() to it.toValue() }
            .toMap(mutableMapOf())
    }


    /** from configs, is used as fallback when we don't have data about the position in the db **/
    private val startBlockByContract: Map<String, Long> =
        contractsConfig.contracts().values.associate { it.address() to it.firstBlockNumber() }

    /**
     * To find start point of reading events from blockchain.
     * It can get data from database or fallback to configs.
     */
    fun getCurrentPosition(processorId: String, contract: String): ProcessedEventPosition {
        val key = PositionKey(processorId, contract)
        return lastPositionByProcessorAndContract[key] ?: run {
            val block: Long = (startBlockByContract[contract]
                ?: error("can't find starting position for $processorId, contract $contract in both database and configs"))

            val position = ProcessedEventPosition(block, null, NEW)
            lastPositionByProcessorAndContract[key] = position

            position
        }
    }

    @Transactional
    fun updatePosition(position: ProcessorLastScannedEventPositionEntity) {
        val entity = ProcessorLastScannedEventPositionEntity
            .find("processorId = ?1 AND contract = ?2", position.processorId, position.contract)
            .firstResult()
            ?.apply {
                this.block = position.block
                this.offset = position.offset
                state = position.state
            } ?: position

        entity.persist()

        lastPositionByProcessorAndContract[entity.toKey()] = entity.toValue()
    }

    private data class PositionKey(
        val processorId: String,
        val contract: String,
    )

    private fun ProcessorLastScannedEventPositionEntity.toKey(): PositionKey = PositionKey(
        processorId = processorId, contract = contract
    )

    private fun ProcessorLastScannedEventPositionEntity.toValue(): ProcessedEventPosition = ProcessedEventPosition(
        block = block, offset = offset, currentState = state,
    )

}