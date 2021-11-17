package nft.freeport.processor.freeport

import nft.freeport.listener.event.EventEntity
import nft.freeport.listener.event.SmartContractEvent

interface FreeportEventProcessor<T : SmartContractEvent> {
    val supportedClass: Class<T>

    fun process(event: T, e: EventEntity)
}