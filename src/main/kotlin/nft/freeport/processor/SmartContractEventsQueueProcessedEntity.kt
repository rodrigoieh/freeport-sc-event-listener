package nft.freeport.processor

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "sc_events_queue_processed")
class SmartContractEventsQueueProcessedEntity(
    @Id
    val id: SmartContractEventsQueueProcessedEntityId
) : PanacheEntityBase