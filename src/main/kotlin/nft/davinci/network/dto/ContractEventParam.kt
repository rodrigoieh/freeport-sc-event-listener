package nft.davinci.network.dto

data class ContractEventParam(
    val name: String,
    val type: String,
    val indexed: Boolean,
    val decoded: Boolean,
    val value: String
)
