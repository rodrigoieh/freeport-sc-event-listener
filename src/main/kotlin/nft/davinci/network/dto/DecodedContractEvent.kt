package nft.davinci.network.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class DecodedContractEvent(
    val name: String,
    val params: List<ContractEventParam>
) {
    private val paramsMap = params.asSequence()
        .map { it.name to it.value }
        .toMap()

    private inline fun <reified T> getParamValue(paramName: String, transform: JsonNode.() -> T): T {
        return this.paramsMap.getValue(paramName).transform()
    }

    fun getParamStringValue(paramName: String): String = getParamValue(paramName, JsonNode::textValue)

    fun getParamArrayValues(paramName: String): List<String> = getParamValue(paramName) { this as ArrayNode }
        .map { it["value"].textValue() }
}
