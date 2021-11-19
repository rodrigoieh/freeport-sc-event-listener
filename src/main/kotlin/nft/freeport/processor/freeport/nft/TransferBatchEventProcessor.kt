package nft.freeport.processor.freeport.nft

import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.TransferBatch
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class TransferBatchEventProcessor(private val nftEventProcessor: NftEventProcessor) :
    FreeportEventProcessor<TransferBatch> {
    override val supportedClass = TransferBatch::class.java

    @Transactional
    override fun process(event: TransferBatch, e: SmartContractEventEntity) {
        event.ids.indices.forEach { i ->
            nftEventProcessor.updateQuantity(event.from, event.ids[i], -event.amounts[i])
            nftEventProcessor.updateQuantity(event.to, event.ids[i], event.amounts[i])
        }
    }
}
