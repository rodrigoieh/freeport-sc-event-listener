package nft.freeport.processor.cms.price

import nft.freeport.listener.event.MakeOffer
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsExistingNftRelatedEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import javax.enterprise.context.ApplicationScoped
import nft.freeport.processor.cms.price.MakeOffer as MakeOfferStrapiCreateRequest

@ApplicationScoped
class MakeOfferEventProcessor(override val strapiService: StrapiService) :
    CmsExistingNftRelatedEventProcessor<MakeOffer> {
    override val supportedClass = MakeOffer::class.java

    override fun process(eventData: SmartContractEventData<out MakeOffer>, nftId: Long, minter: String) {
        strapiService.create(
            route = CmsConfig.Routes::makeOffer,
            payload = MakeOfferStrapiCreateRequest(
                nftId = nftId,
                seller = eventData.event.seller,
                price = eventData.event.price
            )
        )
    }
}
