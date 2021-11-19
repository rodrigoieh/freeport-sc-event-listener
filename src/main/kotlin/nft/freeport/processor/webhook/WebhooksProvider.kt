package nft.freeport.processor.webhook

import io.vertx.core.json.JsonObject
import nft.freeport.processor.webhook.config.WebhooksConfig
import nft.freeport.processor.webhook.impl.strapi.StrapiWebhook
import javax.enterprise.inject.Produces

class WebhooksProvider {
    @Produces
    fun webhooks(config: WebhooksConfig): List<Webhook> {
        if (!config.enabled()) {
            return emptyList()
        }
        return config.webhooks().map {
            when (it.type()) {
                SupportedIntegration.STRAPI -> StrapiWebhook(
                    it.name(),
                    it.baseUrl(),
                    JsonObject(it.config()),
                    JsonObject(it.entities()).map.mapValues { it.value.toString() },
                )
            }
        }
    }
}