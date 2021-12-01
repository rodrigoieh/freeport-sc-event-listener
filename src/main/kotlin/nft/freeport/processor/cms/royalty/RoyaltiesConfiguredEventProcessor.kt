package nft.freeport.processor.cms.royalty

import nft.freeport.listener.event.RoyaltiesConfigured
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RoyaltiesConfiguredEventProcessor(
    private val strapiService: StrapiService,
) : CmsEventProcessor<RoyaltiesConfigured> {
    private val log = LoggerFactory.getLogger(javaClass)
    override val supportedClass = RoyaltiesConfigured::class.java

    override fun process(eventData: SmartContractEventData<out RoyaltiesConfigured>) = with(eventData.event) {
        val nft = strapiService.findOne(CmsConfig.Routes::nft, mapOf("nft_id" to nftId))
        if (nft == null) {
            log.warn("Received RoyaltiesConfigured event for non-existing NFT {}. Skip.", nftId)
            return@with
        }

        strapiService.create(
            route = CmsConfig.Routes::nftRoyalty,
            payload = NftRoyalty(
                nftId = nft.getLong("id"),
                beneficiary = primaryRoyaltyAccount,
                saleCut = primaryRoyaltyCut,
                minimumFee = primaryRoyaltyMinimum,
                saleType = TODO()
            )
        )
    }
}
