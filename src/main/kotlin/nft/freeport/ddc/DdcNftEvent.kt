package nft.freeport.ddc

import com.fasterxml.jackson.annotation.JsonProperty
import nft.freeport.event.NftEvent

data class DdcNftEvent(
    @field:JsonProperty("event_type")
    val eventType: String,
    val id: String,
    val timestamp: String,
    val payload: NftEvent
)
