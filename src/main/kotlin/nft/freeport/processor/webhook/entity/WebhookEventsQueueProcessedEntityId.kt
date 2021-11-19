package nft.freeport.processor.webhook.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class WebhookEventsQueueProcessedEntityId(
    @Column(name = "event_id")
    val eventId: Long,

    @Column(name = "wh_name")
    val webhookName: String
) : Serializable