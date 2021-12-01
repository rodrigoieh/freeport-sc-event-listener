package nft.freeport.processor.cms.price

import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TakeOffer
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import nft.freeport.processor.cms.price.TakeOffer as TakeOfferStrapiCreateRequest

@ApplicationScoped
class TakeOfferEventProcessor(
    private val strapiService: StrapiService,
) : CmsEventProcessor<TakeOffer> {
    private val log = LoggerFactory.getLogger(javaClass)
    override val supportedClass = TakeOffer::class.java

    override fun process(eventData: SmartContractEventData<out TakeOffer>) = with(eventData.event) {
        val nftId = strapiService.findId(CmsConfig.Routes::nft, mapOf("nft_id" to nftId))
        if (nftId == null) {
            log.warn("Received TakeOffer event for non-existing NFT {}. Skip.", this.nftId)
            return@with
        }

        strapiService.create(
            route = CmsConfig.Routes::takeOffer,
            payload = TakeOfferStrapiCreateRequest(
                nftId = nftId,
                seller = seller,
                buyer = buyer,
                price = price,
                amount = amount
            )
        )
    }
}
