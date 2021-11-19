package nft.freeport.processor.webhook.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "wh_events_queue_processed")
class WebhookEventsQueueProcessedEntity(
    @Id
    val id: WebhookEventsQueueProcessedEntityId
) : PanacheEntityBase