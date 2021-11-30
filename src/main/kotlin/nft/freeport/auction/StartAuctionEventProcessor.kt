package nft.freeport.auction

import nft.freeport.event.StartAuction
import nft.freeport.network.processor.EventProcessor
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
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
            nextBidPrice = addTenPercent(event.price),
            endsAt = Instant.ofEpochSecond(event.closeTimeSec.longValueExact()),
            isSettled = false
        ).persist()
    }

    private val priceRoundingMathContext = MathContext(10)
    private val priceMultiplier: BigDecimal = BigDecimal.valueOf(1.1)

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