package nft.freeport.processor

import nft.freeport.NO_EVENTS_BLOCK_OFFSET
import nft.freeport.listener.event.BlockProcessedEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.processorsPosition.ProcessorsPositionManager
import nft.freeport.listener.processorsPosition.dto.ProcessedEventPosition
import nft.freeport.listener.processorsPosition.dto.ProcessingBlockState
import nft.freeport.listener.processorsPosition.dto.ProcessingBlockState.DONE
import nft.freeport.listener.processorsPosition.dto.ProcessingBlockState.PARTIALLY_DONE
import nft.freeport.listener.processorsPosition.entity.ProcessorLastScannedEventPositionEntity
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import kotlin.reflect.KClass

@ApplicationScoped
abstract class EventProcessor(private val stateProvider: ProcessorsPositionManager) {
    abstract val id: String
    internal val supportedEvents: Set<KClass<out SmartContractEvent>> =
        SmartContractEvent::class.sealedSubclasses.filter { it != BlockProcessedEvent::class }
            .toSet()

    abstract fun process(eventData: SmartContractEventData<out SmartContractEvent>)

    @Transactional
    fun processAndCommit(eventData: SmartContractEventData<out SmartContractEvent>) {
        if (eventData.event is BlockProcessedEvent) {
            updatePosition(eventData = eventData, newState = DONE)
            return
        }

        if (!supportedEvents.contains(eventData.event::class)) return

        val currentPosition: ProcessedEventPosition = stateProvider.getCurrentPosition(id, eventData.contract)
        when {
            // handle when current block is before a new one
            currentPosition.block < eventData.rawEvent.blockHeight -> Unit
            // handle when current block == a new one (e.g. in case of failure)
            currentPosition.block == eventData.rawEvent.blockHeight
                    // it wasn't fully processed
                    && currentPosition.currentState != DONE
                    // it's not already processed event
                    && (currentPosition.offset ?: -1) < eventData.rawEvent.logOffset -> Unit

            else -> return
        }

        process(eventData)
        updatePosition(eventData = eventData, newState = PARTIALLY_DONE)
    }

    private fun updatePosition(
        eventData: SmartContractEventData<out SmartContractEvent>,
        newState: ProcessingBlockState
    ) {
        val offset = if (eventData.rawEvent.logOffset == NO_EVENTS_BLOCK_OFFSET) null
        else eventData.rawEvent.logOffset

        stateProvider.updatePosition(
            ProcessorLastScannedEventPositionEntity(
                processorId = id, contract = eventData.contract, state = newState,
                block = eventData.rawEvent.blockHeight, offset = offset
            )
        )
    }
}