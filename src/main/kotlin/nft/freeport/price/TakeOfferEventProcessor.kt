package nft.freeport.price

import nft.freeport.event.TakeOffer
import nft.freeport.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class TakeOfferEventProcessor : EventProcessor<TakeOffer> {
    override val supportedClass = TakeOffer::class.java

    @Transactional
    override fun process(event: TakeOffer) {
        TakeOfferEntity(
            null,
            event.buyer,
            event.seller,
            event.nftId,
            event.price,
            event.amount
        ).persist()
    }
}