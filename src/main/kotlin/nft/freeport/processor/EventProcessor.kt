package nft.freeport.processor

import io.quarkus.vertx.ConsumeEvent
import nft.freeport.SMART_CONTRACT_EVENTS_TOPIC_NAME
import nft.freeport.listener.event.SmartContractEventEntity
import javax.transaction.Transactional

interface EventProcessor {
    val id: Int

    @ConsumeEvent(SMART_CONTRACT_EVENTS_TOPIC_NAME, blocking = true, ordered = true)
    @Transactional
    fun processAndCommit(e: SmartContractEventEntity) {
        process(e)
        commit(requireNotNull(e.id))
    }

    fun process(e: SmartContractEventEntity)

    fun commit(eventId: Long) {
        SmartContractEventsQueueProcessedEntity(SmartContractEventsQueueProcessedEntityId(eventId, id)).persist()
    }
}