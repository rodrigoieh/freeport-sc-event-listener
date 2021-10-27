package nft.davinci.network.processor.nft

import kotlinx.coroutines.coroutineScope
import nft.davinci.event.NftMinted
import nft.davinci.event.NftTransferred
import nft.davinci.event.TransferSingle
import nft.davinci.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferSingleEventProcessor(private val nftEventProcessor: NftEventProcessor) : EventProcessor<TransferSingle> {
    private companion object {
        private const val ZERO_ADDRESS = "0x0000000000000000000000000000000000000000"
    }

    override val supportedClass = TransferSingle::class.java

    override suspend fun process(event: TransferSingle) = coroutineScope {
        val nftEvent = if (event.from == ZERO_ADDRESS) {
            NftMinted(event.operator, event.to, event.id, event.amount)
        } else {
            NftTransferred(event.operator, event.from, event.to, event.id, event.amount)
        }
        nftEventProcessor.onNftEvent(nftEvent, event.blockSignedAt, event.txHash)
    }
}
