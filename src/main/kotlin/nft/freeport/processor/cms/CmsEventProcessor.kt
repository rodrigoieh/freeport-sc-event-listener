package nft.freeport.processor.cms

import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData

interface CmsEventProcessor<T : SmartContractEvent> {
    val supportedClass: Class<T>

    fun process(eventData: SmartContractEventData<out T>)
}