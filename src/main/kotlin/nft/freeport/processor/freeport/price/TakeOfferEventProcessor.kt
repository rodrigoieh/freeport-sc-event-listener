package nft.freeport.processor.freeport.price

import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TakeOffer
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class TakeOfferEventProcessor : FreeportEventProcessor<TakeOffer> {
    override val supportedClass = TakeOffer::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out TakeOffer>) = with(eventData.event) {
        TakeOfferEntity(
            id = null,
            buyer = buyer,
            seller = seller,
            nftId = nftId,
            price = price,
            amount = amount
        ).persist()
    }
}