package nft.freeport.processor.cms.auction

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger
import java.time.Instant

data class AuctionBid(
    @field:JsonProperty("auction_id")
    val auctionId: Long,

    val buyer: String,

    val price: BigInteger,

    val timestamp: Instant,
)
