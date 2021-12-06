package nft.freeport.processor.freeport.auction

import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.StartAuction
import nft.freeport.processor.freeport.FreeportEventProcessor
import nft.freeport.processor.freeport.nft.NftEntity
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class StartAuctionEventProcessor : FreeportEventProcessor<StartAuction> {
    private val log = LoggerFactory.getLogger(javaClass)

    private val priceRoundingMathContext = MathContext(10)
    private val priceMultiplier: BigDecimal = BigDecimal.valueOf(1.1)

    override val supportedClass = StartAuction::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out StartAuction>) = with(eventData.event) {
        if (NftEntity.findById(nftId) == null) {
            log.warn("Received StartAuction event for non-existing NFT {}. Skip.", this.nftId)
            return@with
        }
        AuctionEntity(
            id = null,
            seller = seller,
            buyer = ZERO_ADDRESS,
            nftId = nftId,
            nextBidPrice = addTenPercent(price),
            endsAt = Instant.ofEpochSecond(closeTimeSec.longValueExact()),
            isSettled = false
        ).persist()
    }

    /**
     * [StartAuction.price] isn't price of the next bid, it's expected price, but auction has to contain the next bid price.
     * e.g.
     *  [AuctionEntity.nextBidPrice] 10_000  -   110%  (amount which can be earned after first bid, each bid is 10%)
     *  [StartAuction.price]              ?       -   100%
     *
     *  [StartAuction.price] = 10_000 * 100 / 110 = 9090,9090909091
     *
     *  So it contains such a price, which should be multiplied by 1,1 (add 10%) to get initial price (how much author expects to earn)
     */
    private fun addTenPercent(price: BigInteger): BigInteger =
        price.toBigDecimal().multiply(priceMultiplier, priceRoundingMathContext).toBigInteger()
}