package nft.freeport.network.processor

import nft.freeport.event.SmartContractEvent

interface EventProcessor<T : SmartContractEvent> {
    val supportedClass: Class<T>

    fun process(event: T)
}
