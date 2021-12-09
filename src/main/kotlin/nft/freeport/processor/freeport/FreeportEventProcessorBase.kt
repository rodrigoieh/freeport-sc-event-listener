package nft.freeport.processor.freeport

import nft.freeport.FREEPORT_PROCESSOR_ID
import nft.freeport.listener.event.BlockProcessedEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped
import kotlin.reflect.KClass

@ApplicationScoped
class FreeportEventProcessorBase(
    override val positionManager: ProcessorsPositionManager,
    private val processorsMap: Map<String, FreeportEventProcessor<SmartContractEvent>>,
) : EventProcessor {
    override val id = FREEPORT_PROCESSOR_ID
    override val supportedEvents: Set<KClass<out SmartContractEvent>> =
        SmartContractEvent::class.sealedSubclasses
            .filter { it != BlockProcessedEvent::class }
            .toSet()

    override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {
        processorsMap[eventData.event::class.java.simpleName]?.process(eventData)
    }
}