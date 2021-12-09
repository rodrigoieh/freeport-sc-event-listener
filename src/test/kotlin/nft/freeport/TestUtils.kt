package nft.freeport

import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import java.time.Instant

fun <T : SmartContractEvent> T.wrapEvent(
    blockSignedAt: String = Instant.now().toString(),
    block: Long = 0,
    offset: Long = 42,
): SmartContractEventData<T> =
    SmartContractEventData(
        contract = "some-contract",
        event = this,
        rawEvent = ContractEvent(
            blockSignedAt = blockSignedAt,
            blockHeight = block,
            txHash = "0x0",
            rawLogTopics = emptyList(),
            rawLogData = null,
            decoded = null,
            logOffset = offset,
        )
    )

inline fun buildJsonString(builderAction: JsonObjectBuilder.() -> Unit): String =
    buildJsonObject(builderAction).toString()

inline fun buildJsonArrayString(builderAction: JsonArrayBuilder.() -> Unit): String =
    buildJsonArray(builderAction).toString()
