package nft.freeport.nft

import nft.freeport.event.NftMinted
import nft.freeport.event.NftTransferred
import nft.freeport.event.TransferSingle
import nft.freeport.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferSingleEventProcessor(private val nftEventProcessor: NftEventProcessor) : EventProcessor<TransferSingle> {
    private companion object {
        private const val ZERO_ADDRESS = "0x0000000000000000000000000000000000000000"
    }

    override val supportedClass = TransferSingle::class.java

    override fun process(event: TransferSingle) {
        val nftEvent = if (event.from == ZERO_ADDRESS) {
            NftMinted(event.operator, event.to, event.id, event.amount)
        } else {
            NftTransferred(event.operator, event.from, event.to, event.id, event.amount)
        }
        nftEventProcessor.onNftEvent(nftEvent, event.blockSignedAt, event.txHash)
    }
}
