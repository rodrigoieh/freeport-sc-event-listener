package nft.freeport.processor.cms.nft

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class WalletNft(
    @field:JsonProperty("nft_id")
    val nftId: Long,
    val wallet: String,
    var quantity: BigInteger,
)
