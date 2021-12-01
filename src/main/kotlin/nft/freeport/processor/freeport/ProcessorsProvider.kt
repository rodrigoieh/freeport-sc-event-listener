package nft.freeport.processor.freeport

import nft.freeport.listener.event.SmartContractEvent
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces

class ProcessorsProvider(private val processors: Instance<FreeportEventProcessor<*>>) {
    @Suppress("UNCHECKED_CAST")
    @Produces
    fun processorsMap(): Map<String, FreeportEventProcessor<SmartContractEvent>> {
        return processors.associateBy { it.supportedClass.simpleName } as Map<String, FreeportEventProcessor<SmartContractEvent>>
    }
}
