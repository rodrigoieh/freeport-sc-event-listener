package nft.freeport.processor.cms.nft

import nft.freeport.listener.event.AttachToNFT
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AttachToNFTEventProcessor(private val strapiService: StrapiService) : CmsEventProcessor<AttachToNFT> {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedClass = AttachToNFT::class.java

    override fun process(eventData: SmartContractEventData<out AttachToNFT>) = with(eventData.event) {
        val nft = strapiService.findOne(CmsConfig.Routes::nft, mapOf("nft_id" to nftId))
        if (nft == null) {
            log.warn("Received AttachToNFT event for non-existing NFT {}. Skip.", nftId)
            return@with
        }
        if (!nft.getString("minter").equals(sender, true)) {
            log.warn("Received AttachToNFT event for NFT {} from non-minter {}. Skip.", nftId, sender)
            return@with
        }

        strapiService.create(CmsConfig.Routes::nftCid, NftCid(nft.getLong("id"), sender, cid))
    }
}