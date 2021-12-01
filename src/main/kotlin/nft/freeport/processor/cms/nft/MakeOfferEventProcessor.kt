package nft.freeport.processor.cms.nft

import nft.freeport.listener.event.MakeOffer
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MakeOfferEventProcessor(
    private val strapiService: StrapiService,
) : CmsEventProcessor<MakeOffer> {
    private val log = LoggerFactory.getLogger(javaClass)
    override val supportedClass = MakeOffer::class.java

    override fun process(eventData: SmartContractEventData<out MakeOffer>) = with(eventData.event) {
        val nft = strapiService.findOne(CmsConfig.Routes::nft, mapOf("nft_id" to nftId))
        if (nft == null) {
            log.warn("Received MakeOffer event for non-existing NFT {}. Skip.", nftId)
            return@with
        }

        strapiService.create(
            route = CmsConfig.Routes::makeOffer,
            payload = MakeOfferStrapiModel(
                nftId = nftId.toLong(),
                seller = seller,
                price = price
            )
        )
    }
}
