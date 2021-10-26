package nft.freeport.price

import nft.freeport.event.MakeOffer
import nft.freeport.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class MakeOfferEventProcessor : EventProcessor<MakeOffer> {
    override val supportedClass = MakeOffer::class.java

    @Transactional
    override fun process(event: MakeOffer) {
        val id = MakeOfferEntityId(event.seller, event.nftId)
        val entity = MakeOfferEntity.findById(id) ?: MakeOfferEntity(id, event.price)
        entity.priceInCereTokens = event.price
        entity.persist()
    }
}