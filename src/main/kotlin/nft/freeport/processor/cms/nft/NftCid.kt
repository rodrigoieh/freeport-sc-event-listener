package nft.freeport.processor.cms.nft

import com.fasterxml.jackson.annotation.JsonProperty

data class NftCid(
    @field:JsonProperty("nft_id")
    val nftId: Long,
    val sender: String,
    val cid: String
)
