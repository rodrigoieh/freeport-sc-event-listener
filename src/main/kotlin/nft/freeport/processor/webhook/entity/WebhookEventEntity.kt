package nft.freeport.processor.webhook.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import nft.freeport.processor.webhook.EntityEvent
import javax.persistence.*

@Entity
@Table(name = "wh_events")
class WebhookEventEntity(
    @Id
    @SequenceGenerator(
        name = "whEventsSeq",
        sequenceName = "wh_events_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "whEventsSeq")
    val id: Long?,

    @Column(name = "entity_name")
    val entityName: String,

    @Column
    @Enumerated(EnumType.STRING)
    val event: EntityEvent,

    @Column
    val payload: String,
) : PanacheEntityBase