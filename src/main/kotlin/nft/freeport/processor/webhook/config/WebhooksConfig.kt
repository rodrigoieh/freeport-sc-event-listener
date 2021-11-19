package nft.freeport.processor.webhook.config

import io.smallrye.config.ConfigMapping
import nft.freeport.processor.webhook.SupportedIntegration

@ConfigMapping(prefix = "webhooks")
interface WebhooksConfig {
    fun enabled(): Boolean

    fun webhooks(): List<WebhookConfig>

    interface WebhookConfig {
        fun name(): String

        fun type(): SupportedIntegration

        fun baseUrl(): String

        fun config(): String

        fun entities(): String
    }
}