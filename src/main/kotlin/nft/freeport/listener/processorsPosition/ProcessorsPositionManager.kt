package nft.freeport.listener.processorsPosition

import io.quarkus.runtime.StartupEvent
import nft.freeport.DDC_PROCESSOR_ID
import nft.freeport.listener.config.ContractsConfig
import nft.freeport.listener.processorsPosition.dto.ProcessedEventPosition
import nft.freeport.listener.processorsPosition.dto.ProcessingBlockState.NEW
import nft.freeport.listener.processorsPosition.entity.ProcessorLastScannedEventPositionEntity
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.transaction.Transactional

@ApplicationScoped
class ProcessorsPositionManager(contractsConfig: ContractsConfig) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val lastPositionByProcessorAndContract: MutableMap<PositionKey, ProcessedEventPosition> = mutableMapOf()

    /** Fetch the latest state from the database **/
    @Transactional
    private fun initState(@Observes event: StartupEvent) {
        ProcessorLastScannedEventPositionEntity.findAll().stream().forEach {
            lastPositionByProcessorAndContract[it.toKey()] = it.toValue()
            log.info("The actual processor position: {}", it)
        }
    }

    /**
     * It returns the lowest [ProcessedEventPosition] from all [ProcessorLastScannedEventPositionEntity.processorId].
     */
    fun getCommonChannelMostOutdatedPosition(contract: String): ProcessedEventPosition =
        lastPositionByProcessorAndContract.asSequence()
            .filter { (key, _) -> key.processorId != DDC_PROCESSOR_ID && key.contract == contract }
            .map { it.value }
            .sortedWith(
                compareByDescending<ProcessedEventPosition> { position -> position.block }
                    .thenByDescending { position -> position.offset }
                    .thenByDescending { position -> position.currentState }
            )
            .firstOrNull()
            ?: run {
                val block: Long = startBlockByContract[contract]
                    ?: error("can't find starting position for contract $contract in both database and configs")

                ProcessedEventPosition(block, null, NEW)
            }

    /** from configs, is used as fallback when we don't have data about the position in the db **/
    private val startBlockByContract: Map<String, Long> =
        contractsConfig.contracts().values.associate { it.address() to it.firstBlockNumber() }

    /**
     * To find start point of reading events from blockchain.
     * It can get data from database or fallback to configs.
     */
    fun getCurrentPosition(processorId: String, contract: String): ProcessedEventPosition {
        return lastPositionByProcessorAndContract[PositionKey(processorId, contract)] ?: run {
            val block: Long = (startBlockByContract[contract]
                ?: error("can't find starting position for $processorId, contract $contract in both database and configs"))

            ProcessedEventPosition(block, null, NEW)
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