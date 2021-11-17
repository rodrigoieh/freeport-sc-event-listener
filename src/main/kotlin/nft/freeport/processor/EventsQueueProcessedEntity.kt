package nft.freeport.processor

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "events_queue_processed")
class EventsQueueProcessedEntity(
    @Id
    val id: EventsQueueProcessedEntityId
) : PanacheEntityBase