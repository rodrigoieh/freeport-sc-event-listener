package nft.freeport.processor.cms.auction

import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.StartAuction
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StartAuctionEventProcessor(private val strapiService: StrapiService) : CmsEventProcessor<StartAuction> {
    private val log = LoggerFactory.getLogger(javaClass)

    private val priceRoundingMathContext = MathContext(10)
    private val priceMultiplier: BigDecimal = BigDecimal.valueOf(1.1)

    override val supportedClass = StartAuction::class.java

    override fun process(eventData: SmartContractEventData<out StartAuction>) = with(eventData.event) {
        val nft = strapiService.findId(CmsConfig.Routes::nft, mapOf("nft_id" to nftId))
        if (nft == null) {
            log.warn("Unable to find NFT with id {} in CMS", nftId)
            return@with
        }
        val auction = Auction(
            seller = seller,
            buyer = ZERO_ADDRESS,
            nftId = nft,
            nextBidPrice = addTenPercent(price),
            endsAt = Instant.ofEpochSecond(closeTimeSec.longValueExact()),
            isSettled = false
        )
        strapiService.create(CmsConfig.Routes::auction, auction)
    }

    private fun addTenPercent(price: BigInteger): BigInteger =
        price.toBigDecimal().multiply(priceMultiplier, priceRoundingMathContext).toBigInteger()
}