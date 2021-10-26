package nft.freeport.network.config

interface ContractConfig {
    fun address(): String

    fun firstBlockNumber(): Long

    fun eventTopics(): Map<String, String>
}