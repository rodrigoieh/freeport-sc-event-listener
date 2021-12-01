package nft.freeport.processor.cms.nft

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class TakeOfferStrapiModel(
    @field:JsonProperty("nft_id")
    val nftId: Long,

    val buyer: String,
    val seller: String,

    val price: BigInteger,
    val amount: BigInteger
)
