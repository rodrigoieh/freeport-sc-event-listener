package nft.freeport.processor.cms.price

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class MakeOffer(
    @field:JsonProperty("nft_id")
    val nftId: Long,
    val seller: String,
    val price: BigInteger
)
