package nft.freeport.processor.cms.auction

import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.StartAuction
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsExistingNftRelatedEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StartAuctionEventProcessor(override val strapiService: StrapiService) :
    CmsExistingNftRelatedEventProcessor<StartAuction> {

    private val priceRoundingMathContext = MathContext(10)
    private val priceMultiplier: BigDecimal = BigDecimal.valueOf(1.1)

    override val supportedClass = StartAuction::class.java

    override fun process(eventData: SmartContractEventData<out StartAuction>, nftId: Long, minter: String) {
        val auction = Auction(
            seller = eventData.event.seller,
            buyer = ZERO_ADDRESS,
            nftId = nftId,
            nextBidPrice = addTenPercent(eventData.event.price),
            endsAt = Instant.ofEpochSecond(eventData.event.closeTimeSec.longValueExact()),
            isSettled = false
        )
        strapiService.create(CmsConfig.Routes::auction, auction)
    }

    private fun addTenPercent(price: BigInteger): BigInteger =
        price.toBigDecimal().multiply(priceMultiplier, priceRoundingMathContext).toBigInteger()
}