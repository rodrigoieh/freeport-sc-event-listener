package nft.davinci.network

import io.smallrye.config.ConfigMapping
import java.time.Duration

@ConfigMapping(prefix = "network")
interface NetworkConfig {
    fun chainId(): Int

    fun contractAddress(): String

    fun firstBlockNumber(): Long

    fun covalentApiKey(): String

    fun pollInterval(): Duration

    fun eventTopics(): Map<String, String>
}
