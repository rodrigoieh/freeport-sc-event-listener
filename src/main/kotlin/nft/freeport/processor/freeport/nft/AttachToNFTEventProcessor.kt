package nft.freeport.processor.freeport.nft

import nft.freeport.listener.event.AttachToNFT
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportExistingNftRelatedEventProcessor
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class AttachToNFTEventProcessor : FreeportExistingNftRelatedEventProcessor<AttachToNFT> {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedClass = AttachToNFT::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out AttachToNFT>, nft: NftEntity) = with(eventData.event) {
        if (!nft.minter.equals(sender, true)) {
            log.warn("Received AttachToNFT event for NFT {} from non-minter {}. Skip.", nftId, sender)
            return
        }

        NftCidEntity(id = null, nftId = nftId, sender = sender, cid = cid).persist()
    }
}