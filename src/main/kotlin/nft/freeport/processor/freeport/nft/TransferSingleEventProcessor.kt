package nft.freeport.processor.freeport.nft

import nft.freeport.CURRENCY_TOKEN_ID
import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferSingle
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class TransferSingleEventProcessor(private val nftEventProcessor: NftEventProcessor) :
    FreeportEventProcessor<TransferSingle> {
    override val supportedClass = TransferSingle::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out TransferSingle>) = with(eventData.event) {
        if (from == ZERO_ADDRESS) {
            if (nftId != CURRENCY_TOKEN_ID) {
                NftEntity(nftId, to, amount).persist()
            }
        } else {
            nftEventProcessor.updateQuantity(from, nftId, -amount)
        }
        nftEventProcessor.updateQuantity(to, nftId, amount)
    }
}
