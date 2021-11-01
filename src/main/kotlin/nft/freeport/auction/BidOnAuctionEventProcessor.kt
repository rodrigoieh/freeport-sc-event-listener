package nft.freeport.auction

import nft.freeport.event.BidOnAuction
import nft.freeport.network.processor.EventProcessor
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class BidOnAuctionEventProcessor : EventProcessor<BidOnAuction>  {
    override val supportedClass = BidOnAuction::class.java

    @Transactional
    override fun process(event: BidOnAuction) {
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
            timestamp = Instant.parse(event.blockSignedAt)
        ).persist()
    }
}