package nft.freeport.processor.freeport.price

import nft.freeport.listener.event.MakeOffer
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import nft.freeport.processor.freeport.nft.NftEntity
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class MakeOfferEventProcessor : FreeportEventProcessor<MakeOffer> {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedClass = MakeOffer::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out MakeOffer>) = with(eventData.event) {
        if (NftEntity.findById(nftId) == null) {
            log.warn("Received MakeOffer event for non-existing NFT {}. Skip.", this.nftId)
            return@with
        }
        val id = MakeOfferEntityId(seller, nftId)
        val entity = MakeOfferEntity.findById(id) ?: MakeOfferEntity(id, price)
        entity.priceInCereTokens = price
        entity.persist()
    }
}