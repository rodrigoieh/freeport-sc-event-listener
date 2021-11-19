package nft.freeport.processor.webhook.impl.strapi

import io.vertx.core.json.JsonObject
import nft.freeport.processor.webhook.EntityEvent
import nft.freeport.processor.webhook.Webhook
import nft.freeport.processor.webhook.entity.WebhookEventEntity
import nft.freeport.processor.webhook.impl.strapi.dto.AuthRequest
import org.eclipse.microprofile.rest.client.RestClientBuilder
import java.net.URL

class StrapiWebhook(
    name: String,
    baseUrl: String,
    config: JsonObject,
    entities: Map<String, String>
) : Webhook(name, entities) {
    private companion object {
        private const val TOKEN_TYPE = "Bearer"
    }

    private val client = RestClientBuilder.newBuilder()
        .baseUrl(URL(baseUrl))
        .build(StrapiClient::class.java)
    private val authRequest = AuthRequest(config.getString("login"), config.getString("password"))

    override fun processInternal(webhookEvent: WebhookEventEntity) {
        val auth = client.auth(authRequest).data.token.let { "$TOKEN_TYPE $it" }
        val entityEndpoint = entities.getValue(webhookEvent.entityName)
        when (webhookEvent.event) {
            EntityEvent.CREATED -> client.create(entityEndpoint, webhookEvent.payload, auth)
            EntityEvent.UPDATED -> client.update(entityEndpoint, webhookEvent.payload, auth)
        }
    }
}