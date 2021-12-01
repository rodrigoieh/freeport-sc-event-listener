package nft.freeport.processor.freeport

import nft.freeport.covalent.dto.ContractEvent

fun contractEvent(date: String) = ContractEvent(
    blockSignedAt = date,
    blockHeight = 0,
    txHash = "",
    rawLogTopics = emptyList(),
    rawLogData = "",
    decoded = null,
    logOffset = 0
)