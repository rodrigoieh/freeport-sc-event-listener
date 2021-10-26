package nft.freeport.network.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Block(
    @field:JsonProperty("signed_at")
    val signedAt: String,

    @field:JsonProperty("height")
    val height: Long
)
