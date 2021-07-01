package nft.davinci.ddc

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "ddc")
interface DdcConfig {
    fun bootNode(): String

    fun pubKeyHex(): String

    fun secKeyHex(): String
}
