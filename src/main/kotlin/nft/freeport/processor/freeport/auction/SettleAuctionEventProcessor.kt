package nft.freeport.processor.freeport.auction

import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.SettleAuction
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class SettleAuctionEventProcessor : FreeportEventProcessor<SettleAuction> {
    override val supportedClass = SettleAuction::class.java

    @Transactional
    override fun process(event: SettleAuction, e: SmartContractEventEntity) {
        AuctionEntity.findActive(event.seller, event.nftId).apply {
            buyer = event.buyer
            price = event.price
            endsAt = e.timestamp
            isSettled = true
        }.persist()
    }
}