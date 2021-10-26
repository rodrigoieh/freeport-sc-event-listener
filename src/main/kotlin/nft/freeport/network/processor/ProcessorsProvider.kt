package nft.freeport.network.processor

import nft.freeport.event.SmartContractEvent
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces

class ProcessorsProvider(private val processors: Instance<EventProcessor<*>>) {
    @Suppress("UNCHECKED_CAST")
    @Produces
    fun processorsMap(): Map<String, EventProcessor<SmartContractEvent>> {
        return processors.associateBy { it.supportedClass.simpleName } as Map<String, EventProcessor<SmartContractEvent>>
    }
}
