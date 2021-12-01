package nft.freeport.processor.cms.nft

import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferBatch
import nft.freeport.processor.cms.CmsEventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferBatchEventProcessor(private val nftEventProcessor: NftEventProcessor) :
    CmsEventProcessor<TransferBatch> {
    override val supportedClass = TransferBatch::class.java

    override fun process(eventData: SmartContractEventData<out TransferBatch>) = with(eventData.event) {
        ids.indices.forEach { i ->
            nftEventProcessor.updateQuantity(from, ids[i], -amounts[i])
            nftEventProcessor.updateQuantity(to, ids[i], amounts[i])
        }
    }
}
