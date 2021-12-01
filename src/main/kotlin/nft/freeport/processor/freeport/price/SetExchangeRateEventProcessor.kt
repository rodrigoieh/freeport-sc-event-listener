package nft.freeport.processor.freeport.price

import nft.freeport.listener.event.SetExchangeRate
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class SetExchangeRateEventProcessor : FreeportEventProcessor<SetExchangeRate> {
    override val supportedClass = SetExchangeRate::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out SetExchangeRate>) {
        ExchangeRateEntity.update("cereUnitsPerPenny = ?1", eventData.event.cereUnitsPerPenny)
    }
}