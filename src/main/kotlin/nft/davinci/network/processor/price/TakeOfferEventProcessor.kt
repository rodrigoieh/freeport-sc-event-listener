package nft.davinci.network.processor.price

import nft.davinci.event.TakeOffer
import nft.davinci.network.processor.EventProcessor
import nft.davinci.price.PriceRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TakeOfferEventProcessor(private val priceRepository: PriceRepository) : EventProcessor<TakeOffer> {
    override val supportedClass = TakeOffer::class.java

    override suspend fun process(event: TakeOffer) {
        priceRepository.createTakeOffer(event)
    }
}