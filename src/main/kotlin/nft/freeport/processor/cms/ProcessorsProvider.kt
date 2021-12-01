package nft.freeport.processor.cms

import nft.freeport.listener.event.SmartContractEvent
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces

class ProcessorsProvider(private val processors: Instance<CmsEventProcessor<*>>) {
    @Suppress("UNCHECKED_CAST")
    @Produces
    fun processorsMap(): Map<String, CmsEventProcessor<SmartContractEvent>> {
        return processors.associateBy { it.supportedClass.simpleName } as Map<String, CmsEventProcessor<SmartContractEvent>>
    }
}
