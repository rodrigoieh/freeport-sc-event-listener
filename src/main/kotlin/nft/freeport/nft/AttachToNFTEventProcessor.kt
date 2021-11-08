package nft.freeport.nft

import nft.freeport.event.AttachToNFT
import nft.freeport.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class AttachToNFTEventProcessor : EventProcessor<AttachToNFT> {
    override val supportedClass = AttachToNFT::class.java

    @Transactional
    override fun process(event: AttachToNFT) {
        NftCidEntity(
            id = null,
            nftId = event.nftId,
            sender = event.sender,
            cid = event.cid
        ).persist()
    }
}