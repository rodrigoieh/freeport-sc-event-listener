package nft.freeport.processor.freeport.auction

import nft.freeport.listener.event.SettleAuction
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class SettleAuctionEventProcessor : FreeportEventProcessor<SettleAuction> {
    override val supportedClass = SettleAuction::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out SettleAuction>) = with(eventData) {
        AuctionEntity.findActive(event.seller, event.nftId).apply {
            buyer = event.buyer
            nextBidPrice = event.price
            endsAt = Instant.parse(rawEvent.blockSignedAt)
            isSettled = true
        }.persist()
    }
}