package nft.freeport.network.processor

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.SmartContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class ProcessorsProviderTest {
    @Inject
    internal lateinit var processorsMap: Map<String, EventProcessor<SmartContractEvent>>

    @Test
    fun `Has processors for all supported events`() {
        //given
        val supportedEvents = SmartContractEvent::class.sealedSubclasses
            .map { it.simpleName }
            .sortedBy { it }

        //when
        val eventsWithProcessors = processorsMap.keys.sorted()

        //then
        assertThat(eventsWithProcessors, equalTo(supportedEvents))
    }
}
