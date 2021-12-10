package nft.freeport.processor.cms.price

import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TakeOffer
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsExistingNftRelatedEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import javax.enterprise.context.ApplicationScoped
import nft.freeport.processor.cms.price.TakeOffer as TakeOfferStrapiCreateRequest

@ApplicationScoped
class TakeOfferEventProcessor(override val strapiService: StrapiService) :
    CmsExistingNftRelatedEventProcessor<TakeOffer> {
    override val supportedClass = TakeOffer::class.java

    override fun process(eventData: SmartContractEventData<out TakeOffer>, nftId: Long, minter: String) {
        strapiService.create(
            route = CmsConfig.Routes::takeOffer,
            payload = TakeOfferStrapiCreateRequest(
                nftId = nftId,
                seller = eventData.event.seller,
                buyer = eventData.event.buyer,
                price = eventData.event.price,
                amount = eventData.event.amount
            )
        )
    }
}
