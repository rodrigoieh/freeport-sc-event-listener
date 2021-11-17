package nft.freeport.processor.freeport

import nft.freeport.listener.event.EventEntity
import java.time.Instant

fun eventEntity(date: String) = EventEntity(1, "", "", Instant.parse(date), "")