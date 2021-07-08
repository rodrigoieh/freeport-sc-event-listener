package nft.davinci.network.converter

import nft.davinci.event.SmartContractEvent
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces

@ApplicationScoped
class ConvertersProvider(private val converters: Instance<DecodedContractEventConverter<*>>) {
    @Suppress("UNCHECKED_CAST")
    @Produces
    fun convertersMap(): Map<String, DecodedContractEventConverter<SmartContractEvent>> {
        return converters.associateBy { it.supportedClass.simpleName } as Map<String, DecodedContractEventConverter<SmartContractEvent>>
    }
}
