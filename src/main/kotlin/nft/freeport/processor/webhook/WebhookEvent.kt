package nft.freeport.processor.webhook

data class WebhookEvent(
    val entityName: String,
    val event: EntityEvent,
    val payload: String,
)
