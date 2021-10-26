package nft.freeport.nft

import nft.freeport.event.NftTransferred
import nft.freeport.event.TransferBatch
import nft.freeport.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferBatchEventProcessor(private val nftEventProcessor: NftEventProcessor) : EventProcessor<TransferBatch> {
    override val supportedClass = TransferBatch::class.java

    override fun process(event: TransferBatch) {
        event.ids.indices.forEach { i ->
            val nftEvent = NftTransferred(event.operator, event.from, event.to, event.ids[i], event.amounts[i])
            nftEventProcessor.onNftEvent(nftEvent, event.blockSignedAt, event.txHash)
        }
    }
}
