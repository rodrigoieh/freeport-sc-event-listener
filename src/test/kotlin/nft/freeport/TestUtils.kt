package nft.freeport

import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import java.time.Instant

fun <T : SmartContractEvent> T.wrapEvent(): SmartContractEventData<T> = SmartContractEventData(
    contract = "some-contract",
    event = this,
    rawEvent = ContractEvent(
        blockSignedAt = Instant.now().toString(),
        blockHeight = 0,
        txHash = "0x0",
        rawLogTopics = emptyList(),
        rawLogData = null,
        decoded = null,
        logOffset = 42
    )
)