package nft.freeport.processor.cms.nft

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class NftRoyalty(
    @field:JsonProperty("nft_id")
    val nftId: String,
    @field:JsonProperty("sale_type")
    val saleType: BigInteger,
    val beneficiary: String,
    @field:JsonProperty("sale_cut")
    val saleCut: Int,
    @field:JsonProperty("minimum_fee")
    val minimumFee: BigInteger,
)
