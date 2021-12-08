package nft.freeport.processor.cms

import nft.freeport.CMS_PROCESSOR_ID
import nft.freeport.listener.event.BlockProcessedEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped
import kotlin.reflect.KClass

@ApplicationScoped
class CmsEventProcessorBase(
    override val stateProvider: ProcessorsPositionManager,
    private val processorsMap: Map<String, CmsEventProcessor<SmartContractEvent>>,
) : EventProcessor {
    override val id = CMS_PROCESSOR_ID
    override val supportedEvents: Set<KClass<out SmartContractEvent>> =
        SmartContractEvent::class.sealedSubclasses
            .filter { it != BlockProcessedEvent::class }
            .toSet()

    override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {
        processorsMap[eventData.event::class.java.simpleName]?.process(eventData)
    }
}