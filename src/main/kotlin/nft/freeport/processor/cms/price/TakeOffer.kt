package nft.freeport.processor.cms.price

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class TakeOffer(
    @field:JsonProperty("nft_id")
    val nftId: Long,

    val buyer: String,
    val seller: String,

    val price: BigInteger,
    val amount: BigInteger
)
