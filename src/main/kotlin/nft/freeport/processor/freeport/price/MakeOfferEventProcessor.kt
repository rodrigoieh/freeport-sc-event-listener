package nft.freeport.processor.freeport.price

import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.MakeOffer
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class MakeOfferEventProcessor : FreeportEventProcessor<MakeOffer> {
    override val supportedClass = MakeOffer::class.java

    @Transactional
    override fun process(event: MakeOffer, e: SmartContractEventEntity) {
        val id = MakeOfferEntityId(event.seller, event.nftId)
        val entity = MakeOfferEntity.findById(id) ?: MakeOfferEntity(id, event.price)
        entity.priceInCereTokens = event.price
        entity.persist()
    }
}