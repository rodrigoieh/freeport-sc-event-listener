package nft.davinci.network.processor

import nft.davinci.event.SmartContractEvent
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces

@ApplicationScoped
class ProcessorsProvider(private val processors: Instance<EventProcessor<*>>) {
    @Suppress("UNCHECKED_CAST")
    @Produces
    fun processorsMap(): Map<String, EventProcessor<SmartContractEvent>> {
        return processors.associateBy { it.supportedClass.simpleName } as Map<String, EventProcessor<SmartContractEvent>>
    }
}
