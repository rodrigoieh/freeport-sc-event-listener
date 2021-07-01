package nft.davinci.network.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CovalentResponseData<T>(
    @field:JsonProperty("updated_at")
    val updatedAt: String,

    val items: List<T>
)
