package nft.freeport.processor.freeport.nft

import nft.freeport.listener.event.AttachToNFT
import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.processor.freeport.FreeportEventProcessor
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class AttachToNFTEventProcessor : FreeportEventProcessor<AttachToNFT> {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedClass = AttachToNFT::class.java

    @Transactional
    override fun process(event: AttachToNFT, e: SmartContractEventEntity) {
        if (NftEntity.findById(event.nftId) == null) {
            log.warn("Received AttachToNFT event for non-existing NFT {}. Skip.", event)
            return
        }
        NftCidEntity(
            id = null,
            nftId = event.nftId,
            sender = event.sender,
            cid = event.cid
        ).persist()
    }
}