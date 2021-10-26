package nft.freeport.ddc

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "ddc")
interface DdcConfig {
    fun enabled(): Boolean

    fun bootNode(): String

    fun pubKeyHex(): String

    fun secKeyHex(): String
}
