package nft.davinci.wallet

import com.fasterxml.jackson.annotation.JsonIgnore

data class WalletNft(
    @field:JsonIgnore
    val id: Long,
    val wallet: String,
    val nftId: String,
    val quantity: Long = 0
)
