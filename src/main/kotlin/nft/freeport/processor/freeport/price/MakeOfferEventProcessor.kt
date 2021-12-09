package nft.freeport.processor.freeport.price

import nft.freeport.listener.event.MakeOffer
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportExistingNftRelatedEventProcessor
import nft.freeport.processor.freeport.nft.NftEntity
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class MakeOfferEventProcessor : FreeportExistingNftRelatedEventProcessor<MakeOffer> {
    override val supportedClass = MakeOffer::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out MakeOffer>, nft: NftEntity) = with(eventData.event) {
        val id = MakeOfferEntityId(seller, nftId)
        val entity = MakeOfferEntity.findById(id) ?: MakeOfferEntity(id, price)
        entity.priceInCereTokens = price
        entity.persist()
    }
}