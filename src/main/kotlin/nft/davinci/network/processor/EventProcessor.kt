package nft.davinci.network.processor

import nft.davinci.event.SmartContractEvent

interface EventProcessor<T : SmartContractEvent> {
    val supportedClass: Class<T>

    suspend fun process(event: T)
}
