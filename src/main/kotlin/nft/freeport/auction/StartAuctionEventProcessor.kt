package nft.freeport.auction

import nft.freeport.event.StartAuction
import nft.freeport.network.processor.EventProcessor
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class StartAuctionEventProcessor : EventProcessor<StartAuction> {
    private companion object {
        private const val ZERO_ADDRESS = "0x0000000000000000000000000000000000000000"
    }

    override val supportedClass = StartAuction::class.java

    @Transactional
    override fun process(event: StartAuction) {
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