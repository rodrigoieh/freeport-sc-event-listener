package nft.davinci.db

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithName

@ConfigMapping(prefix = "quarkus.datasource")
interface DataSourceConfig {
    fun username(): String

    fun password(): String

    @WithName("reactive.url")
    fun url(): String

    fun dbKind(): String

    fun jdbc(): Boolean
}
