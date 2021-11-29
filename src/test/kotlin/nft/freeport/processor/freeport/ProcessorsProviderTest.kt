package nft.freeport.processor.freeport

import io.kotest.matchers.collections.shouldContainExactly
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.AbstractIntegrationTest
import nft.freeport.listener.event.BlockProcessedEvent
import nft.freeport.listener.event.SmartContractEvent
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class ProcessorsProviderTest : AbstractIntegrationTest() {
    @Inject
    internal lateinit var processorsMap: Map<String, FreeportEventProcessor<SmartContractEvent>>

    @Test
    fun `Has processors for all supported events`() {
        //given
        val supportedEvents = SmartContractEvent::class.sealedSubclasses
            // tech event should not have special freeport processor
            .filter { it != BlockProcessedEvent::class }
            .map { it.simpleName }
            .sortedBy { it }

        //when
        val eventsWithProcessors = processorsMap.keys.sorted()

        //then
        eventsWithProcessors shouldContainExactly supportedEvents
    }
}