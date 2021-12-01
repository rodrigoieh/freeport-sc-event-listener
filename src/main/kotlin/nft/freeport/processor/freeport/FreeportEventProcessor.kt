package nft.freeport.processor.freeport

import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData

interface FreeportEventProcessor<T : SmartContractEvent> {
    val supportedClass: Class<T>

    fun process(eventData: SmartContractEventData<out T>)
}