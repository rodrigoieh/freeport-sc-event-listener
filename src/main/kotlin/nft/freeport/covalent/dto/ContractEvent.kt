package nft.freeport.covalent.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContractEvent(
    @field:JsonProperty("block_signed_at")
    val blockSignedAt: String,

    @field:JsonProperty("block_height")
    val blockHeight: Long,

    @field:JsonProperty("tx_hash")
    val txHash: String,

    @field:JsonProperty("raw_log_topics")
    val rawLogTopics: List<String>,

    @field:JsonProperty("raw_log_data")
    val rawLogData: String?,

    val decoded: DecodedContractEvent?
)
