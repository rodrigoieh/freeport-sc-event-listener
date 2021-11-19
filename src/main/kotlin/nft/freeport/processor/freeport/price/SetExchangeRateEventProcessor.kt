package nft.freeport.processor.freeport.price

import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.SetExchangeRate
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class SetExchangeRateEventProcessor : FreeportEventProcessor<SetExchangeRate> {
    override val supportedClass = SetExchangeRate::class.java

    @Transactional
    override fun process(event: SetExchangeRate, e: SmartContractEventEntity) {
        ExchangeRateEntity.update("cereUnitsPerPenny = ?1", event.cereUnitsPerPenny)
    }
}