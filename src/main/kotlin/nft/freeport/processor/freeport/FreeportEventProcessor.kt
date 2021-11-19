package nft.freeport.processor.freeport

import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.SmartContractEvent

interface FreeportEventProcessor<T : SmartContractEvent> {
    val supportedClass: Class<T>

    fun process(event: T, e: SmartContractEventEntity)
}