package nft.davinci.network.converter

import io.quarkus.test.junit.QuarkusTest
import nft.davinci.event.SmartContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class ConvertersProviderTest {
    @Inject
    internal lateinit var convertersMap: Map<String, DecodedContractEventConverter<SmartContractEvent>>

    @Test
    fun `Has converters for all supported events`() {
        //given
        val supportedEvents = SmartContractEvent::class.sealedSubclasses
            .map { it.simpleName }
            .sortedBy { it }

        //when
        val eventsWithConverters = convertersMap.keys.sorted()

        //then
        assertThat(eventsWithConverters, equalTo(supportedEvents))
    }
}
