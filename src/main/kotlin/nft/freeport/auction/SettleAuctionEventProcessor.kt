package nft.freeport.auction

import nft.freeport.event.SettleAuction
import nft.freeport.network.processor.EventProcessor
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class SettleAuctionEventProcessor : EventProcessor<SettleAuction> {
    override val supportedClass = SettleAuction::class.java

    @Transactional
    override fun process(event: SettleAuction) {
        AuctionEntity.findActive(event.seller, event.nftId).apply {
            buyer = event.buyer
            price = event.price
            endsAt = Instant.parse(event.blockSignedAt)
            isSettled = true
        }.persist()
    }
}