package nft.davinci.ddc

import com.fasterxml.jackson.annotation.JsonProperty
import nft.davinci.event.NftEvent

data class DdcNftEvent(
    @field:JsonProperty("event_type")
    val eventType: String,
    val id: String,
    val timestamp: String,
    val payload: NftEvent
)
