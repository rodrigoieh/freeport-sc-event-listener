package nft.freeport.processor.freeport.nft

import nft.freeport.listener.event.AttachToNFT
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class AttachToNFTEventProcessor : FreeportEventProcessor<AttachToNFT> {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedClass = AttachToNFT::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out AttachToNFT>) = with(eventData.event) {
        if (NftEntity.findById(nftId) == null) {
            log.warn("Received AttachToNFT event for non-existing NFT {}. Skip.", this)
            return
        }

        NftCidEntity(
            id = null,
            nftId = nftId,
            sender = sender,
            cid = cid
        ).persist()
    }
}