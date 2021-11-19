package nft.freeport.processor.freeport

import com.fasterxml.jackson.databind.ObjectMapper
import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FreeportEventProcessorBase(
    private val objectMapper: ObjectMapper,
    private val processorsMap: Map<String, FreeportEventProcessor<SmartContractEvent>>
) : EventProcessor {
    private val eventClasses: Map<String, Class<out SmartContractEvent>> = SmartContractEvent::class
        .sealedSubclasses
        .map { it.java }
        .associateBy { it.simpleName }

    override val id = 1

    override fun process(e: SmartContractEventEntity) {
        val eventClass = eventClasses.getValue(e.name)
        val event = objectMapper.readValue(e.payload, eventClass)
        processorsMap[e.name]?.process(event, e)
    }
}