package nft.davinci.network.processor.nft

import nft.davinci.event.NftTransferred
import nft.davinci.event.TransferBatch
import nft.davinci.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferBatchEventProcessor(private val nftEventProcessor: NftEventProcessor) : EventProcessor<TransferBatch> {
    override val supportedClass = TransferBatch::class.java

    override suspend fun process(event: TransferBatch) {
        event.ids.indices.forEach { i ->
            val nftEvent = NftTransferred(event.operator, event.from, event.to, event.ids[i], event.amounts[i])
            nftEventProcessor.onNftEvent(nftEvent, event.blockSignedAt, event.txHash)
        }
    }
}
