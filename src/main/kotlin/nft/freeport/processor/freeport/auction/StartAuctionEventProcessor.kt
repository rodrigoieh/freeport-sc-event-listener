package nft.freeport.processor.freeport.auction

import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.StartAuction
import nft.freeport.processor.freeport.FreeportEventProcessor
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class StartAuctionEventProcessor : FreeportEventProcessor<StartAuction> {
    override val supportedClass = StartAuction::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out StartAuction>) = with(eventData.event) {
        AuctionEntity(
            id = null,
            seller = seller,
            buyer = ZERO_ADDRESS,
            nftId = nftId,
            price = price,
            endsAt = Instant.ofEpochSecond(closeTimeSec.longValueExact()),
            isSettled = false
        ).persist()
    }
}