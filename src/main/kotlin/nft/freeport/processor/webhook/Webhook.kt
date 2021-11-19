package nft.freeport.processor.webhook

import nft.freeport.processor.webhook.entity.WebhookEventEntity
import org.slf4j.LoggerFactory

abstract class Webhook(
    val name: String,
    val entities: Map<String, String>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun process(webhookEvent: WebhookEventEntity) {
        val entityName = webhookEvent.entityName
        val entityEndpoint = entities[entityName]
        if (entityEndpoint == null) {
            log.debug("Entity {} is not in the list of entities for webhook {}", entityName, name)
            return
        }
        log.info("Calling webhook {} for entity {} {}", name, entityName, webhookEvent.event)
        processInternal(webhookEvent)
    }

    abstract fun processInternal(webhookEvent: WebhookEventEntity)
}