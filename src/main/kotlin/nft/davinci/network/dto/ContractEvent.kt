package nft.davinci.network.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContractEvent(
    @field:JsonProperty("block_height")
    val blockHeight: Long,

    val decoded: DecodedContractEvent?
)
