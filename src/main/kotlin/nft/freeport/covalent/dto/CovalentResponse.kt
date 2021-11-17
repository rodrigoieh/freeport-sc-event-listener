package nft.freeport.covalent.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CovalentResponse<T>(
    val data: CovalentResponseData<T>?,

    val error: Boolean,

    @field:JsonProperty("error_message")
    val errorMessage: String?,
)
