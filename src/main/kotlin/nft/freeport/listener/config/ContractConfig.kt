package nft.freeport.listener.config

interface ContractConfig {
    fun address(): String

    fun firstBlockNumber(): Long
}