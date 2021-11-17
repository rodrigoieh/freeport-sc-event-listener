package nft.freeport.processor.freeport.price

import nft.freeport.listener.event.EventEntity
import nft.freeport.listener.event.TakeOffer
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class TakeOfferEventProcessor : FreeportEventProcessor<TakeOffer> {
    override val supportedClass = TakeOffer::class.java

    @Transactional
    override fun process(event: TakeOffer, e: EventEntity) {
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