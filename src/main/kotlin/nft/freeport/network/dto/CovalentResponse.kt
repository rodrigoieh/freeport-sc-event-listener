package nft.freeport.network.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CovalentResponse<T>(
    val data: CovalentResponseData<T>?,

    val error: Boolean,

    @field:JsonProperty("error_message")
    val errorMessage: String?,

    @field:JsonProperty("error_code")
    val errorCode: Int?
)
