package nft.davinci.network.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContractEvent(
    @field:JsonProperty("block_signed_at")
    val blockSignedAt: String,

    @field:JsonProperty("block_height")
    val blockHeight: Long,

    @field:JsonProperty("tx_hash")
    val tx_hash: String,

    @field:JsonProperty("raw_log_topics")
    val rawLogTopics: List<String>,

    @field:JsonProperty("sender_address")
    val senderAddress: String,

    val decoded: DecodedContractEvent
)
