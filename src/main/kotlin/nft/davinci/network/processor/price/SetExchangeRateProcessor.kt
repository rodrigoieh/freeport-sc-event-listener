package nft.davinci.network.processor.price

import nft.davinci.event.SetExchangeRate
import nft.davinci.network.processor.EventProcessor
import nft.davinci.price.PriceRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SetExchangeRateProcessor(private val priceRepository: PriceRepository) : EventProcessor<SetExchangeRate> {
    override val supportedClass = SetExchangeRate::class.java

    override suspend fun process(event: SetExchangeRate) {
        priceRepository.updateExchangeRate(event.cereUnitsPerPenny)
    }
}