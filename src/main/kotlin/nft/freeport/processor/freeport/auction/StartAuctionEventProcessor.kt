package nft.freeport.processor.freeport.auction

import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.EventEntity
import nft.freeport.listener.event.StartAuction
import nft.freeport.processor.freeport.FreeportEventProcessor
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class StartAuctionEventProcessor : FreeportEventProcessor<StartAuction> {
    override val supportedClass = StartAuction::class.java

    @Transactional
    override fun process(event: StartAuction, e: EventEntity) {
        AuctionEntity(
            id = null,
            seller = event.seller,
            buyer = ZERO_ADDRESS,
            nftId = event.nftId,
            price = event.price,
            endsAt = Instant.ofEpochSecond(event.closeTimeSec.longValueExact()),
            isSettled = false
        ).persist()
    }
}