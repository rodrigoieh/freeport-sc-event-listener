package nft.freeport.processor.cms.nft

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class MakeOfferStrapiModel(
    @field:JsonProperty("nft_id")
    val nftId: Long,
    val seller: String,
    val price: BigInteger
)
