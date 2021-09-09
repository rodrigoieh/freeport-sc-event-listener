package nft.davinci.network.processor.price

import nft.davinci.event.MakeOffer
import nft.davinci.network.processor.EventProcessor
import nft.davinci.price.PriceRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MakeOfferEventProcessor(private val priceRepository: PriceRepository) : EventProcessor<MakeOffer> {
    override val supportedClass = MakeOffer::class.java

    override suspend fun process(event: MakeOffer) {
        priceRepository.createOrUpdateMakeOffer(event)
    }
}