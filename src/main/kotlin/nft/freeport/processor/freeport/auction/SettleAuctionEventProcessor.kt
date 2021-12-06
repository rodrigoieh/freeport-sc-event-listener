package nft.freeport.processor.freeport.auction

import nft.freeport.listener.event.SettleAuction
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class SettleAuctionEventProcessor : FreeportEventProcessor<SettleAuction> {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedClass = SettleAuction::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out SettleAuction>) = with(eventData) {
        val auction = AuctionEntity.findActive(event.seller, event.nftId)
        if (auction == null) {
            log.warn("Active auction not found for seller ${event.seller} and nftId ${event.nftId}")
            return@with
        }
        auction.apply {
            buyer = event.buyer
            nextBidPrice = event.price
            endsAt = Instant.parse(rawEvent.blockSignedAt)
            isSettled = true
        }.persist()
    }
}