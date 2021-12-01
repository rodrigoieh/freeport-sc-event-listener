package nft.freeport.listener.config

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithParentName

@ConfigMapping(prefix = "contracts")
interface ContractsConfig {
    @WithParentName
    fun contracts(): Map<String, ContractConfig>
}