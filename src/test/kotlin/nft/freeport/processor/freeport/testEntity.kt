package nft.freeport.processor.freeport

import nft.freeport.listener.event.SmartContractEventEntity
import java.time.Instant

fun eventEntity(date: String) = SmartContractEventEntity(1, "", "", Instant.parse(date), "")