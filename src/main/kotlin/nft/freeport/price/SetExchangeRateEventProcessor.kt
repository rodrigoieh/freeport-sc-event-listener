package nft.freeport.price

import nft.freeport.event.SetExchangeRate
import nft.freeport.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class SetExchangeRateEventProcessor : EventProcessor<SetExchangeRate> {
    override val supportedClass = SetExchangeRate::class.java

    @Transactional
    override fun process(event: SetExchangeRate) {
        ExchangeRateEntity.update("cereUnitsPerPenny = ?1", event.cereUnitsPerPenny)
    }
}