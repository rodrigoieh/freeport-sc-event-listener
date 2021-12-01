package nft.freeport.covalent.config

import io.smallrye.config.ConfigMapping
import java.time.Duration

@ConfigMapping(prefix = "network")
interface NetworkConfig {
    fun chainId(): Int

    fun covalentApiKey(): String

    fun pollInterval(): Duration
}
