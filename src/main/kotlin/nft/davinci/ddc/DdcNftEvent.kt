package nft.davinci.ddc

import com.fasterxml.jackson.annotation.JsonProperty
import nft.davinci.network.dto.NftEvent

data class DdcNftEvent(
    @field:JsonProperty("event_type")
    val eventType: String,

    val payload: NftEvent
)
