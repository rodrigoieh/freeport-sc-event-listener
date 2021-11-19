package nft.freeport.processor.freeport.nft

import nft.freeport.CURRENCY_TOKEN_ID
import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.TransferSingle
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class TransferSingleEventProcessor(private val nftEventProcessor: NftEventProcessor) :
    FreeportEventProcessor<TransferSingle> {
    override val supportedClass = TransferSingle::class.java

    @Transactional
    override fun process(event: TransferSingle, e: SmartContractEventEntity) {
        if (event.from == ZERO_ADDRESS) {
            if (event.nftId != CURRENCY_TOKEN_ID) {
                NftEntity(event.nftId, event.to, event.amount).persist()
            }
        } else {
            nftEventProcessor.updateQuantity(event.from, event.nftId, -event.amount)
        }
        nftEventProcessor.updateQuantity(event.to, event.nftId, event.amount)
    }
}
