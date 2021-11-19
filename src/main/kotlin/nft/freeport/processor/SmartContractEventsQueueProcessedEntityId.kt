package nft.freeport.processor

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class SmartContractEventsQueueProcessedEntityId(
    @Column(name = "event_id")
    val eventId: Long,

    @Column(name = "worker_id")
    val workerId: Int
) : Serializable