package nft.davinci.network.processor.nft

import nft.davinci.event.NftTransferred
import nft.davinci.event.TransferBatch
import nft.davinci.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferBatchEventProcessor(private val nftEventProcessor: NftEventProcessor) : EventProcessor<TransferBatch> {
    override val supportedClass = TransferBatch::class.java

    override suspend fun process(event: TransferBatch) {
        val (operator, from, to, ids, amounts) = event
        ids.indices.forEach { i ->
            val nftEvent = NftTransferred(operator, from, to, ids[i], amounts[i])
            nftEventProcessor.onNftEvent(nftEvent)
        }
    }
}
