package nft.freeport.processor.freeport

import nft.freeport.FREEPORT_PROCESSOR_ID
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.processorsPosition.ProcessorsPositionManager
import nft.freeport.processor.EventProcessor

class FreeportEventProcessorBase(
    private val processorsMap: Map<String, FreeportEventProcessor<SmartContractEvent>>,

    stateProvider: ProcessorsPositionManager,
) : EventProcessor(stateProvider) {
    override val id = FREEPORT_PROCESSOR_ID

    override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {
        processorsMap[eventData.event::class.java.simpleName]?.process(eventData)
    }

}