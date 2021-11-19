package nft.freeport.processor.webhook

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.hibernate.orm.PersistenceUnitExtension
import io.vertx.core.eventbus.EventBus
import nft.freeport.ENTITY_EVENTS_TOPIC_NAME
import nft.freeport.processor.freeport.auction.AuctionBidEntity
import nft.freeport.processor.freeport.auction.AuctionEntity
import nft.freeport.processor.freeport.nft.NftCidEntity
import nft.freeport.processor.freeport.nft.NftEntity
import nft.freeport.processor.freeport.nft.WalletNftEntity
import org.hibernate.EmptyInterceptor
import org.hibernate.type.Type
import java.io.Serializable
import java.util.concurrent.ArrayBlockingQueue

@PersistenceUnitExtension
class DatabaseInterceptor(
    private val objectMapper: ObjectMapper,
    private val bus: EventBus,
) : EmptyInterceptor() {
    private val trackedEntities = listOf(
        AuctionEntity::class,
        AuctionBidEntity::class,
        NftEntity::class,
        NftCidEntity::class,
        WalletNftEntity::class,
    )

    private val pendingEvents = ArrayBlockingQueue<WebhookEvent>(25)

    override fun onSave(
        entity: Any,
        id: Serializable,
        state: Array<out Any>,
        propertyNames: Array<out String>,
        types: Array<out Type>
    ): Boolean {
        if (entity::class in trackedEntities) {
            pendingEvents.add(convert(entity, EntityEvent.CREATED))
        }
        return false
    }

    override fun onFlushDirty(
        entity: Any,
        id: Serializable,
        currentState: Array<out Any>,
        previousState: Array<out Any>,
        propertyNames: Array<out String>,
        types: Array<out Type>
    ): Boolean {
        if (entity::class in trackedEntities) {
            pendingEvents.add(convert(entity, EntityEvent.UPDATED))
        }
        return false
    }

    private fun convert(entity: Any, event: EntityEvent): WebhookEvent {
        return WebhookEvent(
            entityName = entity::class.java.simpleName.removeSuffix("Entity").lowercase(),
            event = event,
            payload = objectMapper.writeValueAsString(entity)
        )
    }

    override fun postFlush(entities: Iterator<Any?>) {
        if (pendingEvents.isEmpty()) {
            return
        }
        val view = mutableListOf<WebhookEvent>()
        pendingEvents.drainTo(view)
        view.forEach { bus.publish(ENTITY_EVENTS_TOPIC_NAME, it) }
    }
}