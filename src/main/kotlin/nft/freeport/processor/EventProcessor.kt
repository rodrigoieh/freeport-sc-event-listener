package nft.freeport.processor

import io.quarkus.vertx.ConsumeEvent
import nft.freeport.EVENTS_TOPIC_NAME
import nft.freeport.listener.event.EventEntity
import javax.transaction.Transactional

interface EventProcessor {
    val id: Int

    @ConsumeEvent(EVENTS_TOPIC_NAME, blocking = true, ordered = true)
    @Transactional
    fun processAndCommit(e: EventEntity) {
        process(e)
        commit(requireNotNull(e.id))
    }

    fun process(e: EventEntity)

    fun commit(eventId: Long) {
        EventsQueueProcessedEntity(EventsQueueProcessedEntityId(eventId, id)).persist()
    }
}