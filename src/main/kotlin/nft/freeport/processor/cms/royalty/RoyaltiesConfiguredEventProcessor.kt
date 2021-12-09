package nft.freeport.processor.cms.royalty

import nft.freeport.listener.event.RoyaltiesConfigured
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsExistingNftRelatedEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RoyaltiesConfiguredEventProcessor(override val strapiService: StrapiService) :
    CmsExistingNftRelatedEventProcessor<RoyaltiesConfigured> {
    override val supportedClass = RoyaltiesConfigured::class.java

    override fun process(eventData: SmartContractEventData<out RoyaltiesConfigured>, nftId: Long, minter: String) {
        create(
            NftRoyalty(
                nftId = nftId,
                saleType = 1,
                beneficiary = eventData.event.primaryRoyaltyAccount,
                saleCut = eventData.event.primaryRoyaltyCut,
                minimumFee = eventData.event.primaryRoyaltyMinimum,
            ), NftRoyalty(
                nftId = nftId,
                saleType = 2,
                beneficiary = eventData.event.secondaryRoyaltyAccount,
                saleCut = eventData.event.secondaryRoyaltyCut,
                minimumFee = eventData.event.secondaryRoyaltyMinimum,
            )
        )
    }

    private fun create(vararg royalties: NftRoyalty) {
        for (royalty in royalties) {
            strapiService.create(
                route = CmsConfig.Routes::nftRoyalty,
                payload = royalty
            )
        }
    }
}
