package nft.freeport.processor.freeport.auction

import nft.freeport.listener.event.BidOnAuction
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class BidOnAuctionEventProcessor : FreeportEventProcessor<BidOnAuction> {
    private val log = LoggerFactory.getLogger(javaClass)
    override val supportedClass = BidOnAuction::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out BidOnAuction>) = with(eventData) {
        val auction = AuctionEntity.findActive(event.seller, event.nftId)
        if (auction == null) {
            log.warn("Active auction not found for seller ${event.seller} and nftId ${event.nftId}")
            return@with
        }
        auction.apply {
            buyer = event.buyer
            nextBidPrice = event.price
            endsAt = Instant.ofEpochSecond(event.closeTimeSec.longValueExact())
        }

        AuctionBidEntity(
            id = null,
            auction = auction,
            buyer = event.buyer,
            price = event.price,
            timestamp = Instant.parse(rawEvent.blockSignedAt)
        ).persist()
    }
}