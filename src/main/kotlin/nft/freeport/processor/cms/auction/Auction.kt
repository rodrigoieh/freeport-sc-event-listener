package nft.freeport.processor.cms.auction

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger
import java.time.Instant

data class Auction(
    val seller: String,

    var buyer: String,

    @field:JsonProperty("nft_id")
    val nftId: Long,

    @field:JsonProperty("price")
    var nextBidPrice: BigInteger,

    @field:JsonProperty("ends_at")
    var endsAt: Instant,

    @field:JsonProperty("is_settled")
    var isSettled: Boolean
)