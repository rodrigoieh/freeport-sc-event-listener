package nft.freeport.processor.webhook

import io.quarkus.vertx.ConsumeEvent
import nft.freeport.ENTITY_EVENTS_TOPIC_NAME
import nft.freeport.processor.webhook.entity.WebhookEventEntity
import nft.freeport.processor.webhook.entity.WebhookEventsQueueProcessedEntity
import nft.freeport.processor.webhook.entity.WebhookEventsQueueProcessedEntityId
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class WebhookProcessor(private val webhooks: List<Webhook>) {
    // TODO we should have separate queue per webhook in future to track failures
    // this is a naive implementation for now while we have only one webhook
    @ConsumeEvent(ENTITY_EVENTS_TOPIC_NAME, blocking = true, ordered = true)
    fun process(webhookEvent: WebhookEvent) {
        val e = persist(webhookEvent)
        webhooks.forEach {
            process(it, e)
        }
    }

    @Transactional
    internal fun persist(webhookEvent: WebhookEvent): WebhookEventEntity {
        val e = WebhookEventEntity(null, webhookEvent.entityName, webhookEvent.event, webhookEvent.payload)
        e.persist()
        return e
    }

    @Transactional
    internal fun process(webhook: Webhook, webhookEvent: WebhookEventEntity) {
        webhook.process(webhookEvent)
        WebhookEventsQueueProcessedEntity(
            WebhookEventsQueueProcessedEntityId(
                requireNotNull(webhookEvent.id),
                webhook.name
            )
        ).persist()
    }
}