package nft.freeport.processor.freeport.auction

import nft.freeport.listener.event.BidOnAuction
import nft.freeport.listener.event.EventEntity
import nft.freeport.processor.freeport.FreeportEventProcessor
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class BidOnAuctionEventProcessor : FreeportEventProcessor<BidOnAuction> {
    override val supportedClass = BidOnAuction::class.java

    @Transactional
    override fun process(event: BidOnAuction, e: EventEntity) {
        val auction = AuctionEntity.findActive(event.seller, event.nftId).apply {
            buyer = event.buyer
            price = event.price
            endsAt = Instant.ofEpochSecond(event.closeTimeSec.longValueExact())
        }
        AuctionBidEntity(
            id = null,
            auction = auction,
            buyer = event.buyer,
            price = event.price,
            timestamp = e.timestamp
        ).persist()
    }
}