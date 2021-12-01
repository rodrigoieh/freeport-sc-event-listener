package nft.freeport.processor.ddc

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "ddc")
interface DdcConfig {
    fun enabled(): Boolean

    fun bootNodes(): List<String>

    fun pubKeyHex(): String

    fun secKeyHex(): String
}
