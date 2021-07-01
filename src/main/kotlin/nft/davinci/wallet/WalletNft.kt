package nft.davinci.wallet

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigInteger

data class WalletNft(
    @field:JsonIgnore
    val id: Long,
    val wallet: String,
    val nftId: String,
    val quantity: BigInteger = BigInteger.ZERO
)
