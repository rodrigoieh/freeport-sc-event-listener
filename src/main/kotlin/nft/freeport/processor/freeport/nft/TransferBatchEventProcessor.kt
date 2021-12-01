package nft.freeport.processor.freeport.nft

import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferBatch
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class TransferBatchEventProcessor(private val nftEventProcessor: NftEventProcessor) :
    FreeportEventProcessor<TransferBatch> {
    override val supportedClass = TransferBatch::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out TransferBatch>) = with(eventData.event) {
        ids.indices.forEach { i ->
            nftEventProcessor.updateQuantity(from, ids[i], -amounts[i])
            nftEventProcessor.updateQuantity(to, ids[i], amounts[i])
        }
    }
}
