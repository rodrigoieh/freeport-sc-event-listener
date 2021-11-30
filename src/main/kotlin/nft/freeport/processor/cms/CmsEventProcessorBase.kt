package nft.freeport.processor.cms

import nft.freeport.CMS_PROCESSOR_ID
import nft.freeport.SMART_CONTRACT_EVENTS_CMS_TOPIC_NAME
import nft.freeport.listener.event.SetExchangeRate
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferSingle
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.processor.EventProcessor
import org.eclipse.microprofile.reactive.messaging.Incoming
import kotlin.reflect.KClass

class CmsEventProcessorBase(
    private val cmsConfig: CmsConfig,
    private val processorsMap: Map<String, CmsEventProcessor<SmartContractEvent>>,
    stateProvider: ProcessorsPositionManager,
) : EventProcessor(stateProvider) {
    override val id = CMS_PROCESSOR_ID
    override val supportedEvents: Set<KClass<out SmartContractEvent>> = setOf(
        TransferSingle::class,
        SetExchangeRate::class
    )

    @Incoming(SMART_CONTRACT_EVENTS_CMS_TOPIC_NAME)
    override fun processAndCommit(eventData: SmartContractEventData<out SmartContractEvent>) =
        super.processAndCommit(eventData)

    override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {
        if (cmsConfig.enabled()) {
            processorsMap[eventData.event::class.java.simpleName]?.process(eventData)
        }
    }
}