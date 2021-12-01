package nft.freeport.processor.cms.nft

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class Nft(
    @field:JsonProperty("nft_id")
    val nftId: String,
    val minter: String,
    val supply: BigInteger
)
