package nft.freeport.processor.cms.nft

import nft.freeport.listener.event.AttachToNFT
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsExistingNftRelatedEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AttachToNFTEventProcessor(override val strapiService: StrapiService) :
    CmsExistingNftRelatedEventProcessor<AttachToNFT> {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedClass = AttachToNFT::class.java

    override fun process(eventData: SmartContractEventData<out AttachToNFT>, nftId: Long, minter: String) {
        if (!minter.equals(eventData.event.sender, true)) {
            log.warn("Received AttachToNFT event for NFT {} from non-minter {}. Skip.", nftId, eventData.event.sender)
            return
        }

        strapiService.create(CmsConfig.Routes::nftCid, NftCid(nftId, eventData.event.sender, eventData.event.cid))
    }
}