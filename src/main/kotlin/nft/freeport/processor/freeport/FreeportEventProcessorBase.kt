package nft.freeport.processor.freeport

import nft.freeport.FREEPORT_PROCESSOR_ID
import nft.freeport.SMART_CONTRACT_EVENTS_FREEPORT_TOPIC_NAME
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.processor.EventProcessor
import org.eclipse.microprofile.reactive.messaging.Incoming

class FreeportEventProcessorBase(
    private val processorsMap: Map<String, FreeportEventProcessor<SmartContractEvent>>,

    stateProvider: ProcessorsPositionManager,
) : EventProcessor(stateProvider) {
    override val id = FREEPORT_PROCESSOR_ID

    @Incoming(SMART_CONTRACT_EVENTS_FREEPORT_TOPIC_NAME)
    override fun processAndCommit(eventData: SmartContractEventData<out SmartContractEvent>) =
        super.processAndCommit(eventData)

    override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {
        processorsMap[eventData.event::class.java.simpleName]?.process(eventData)
    }

}