package nft.freeport.processor.cms.price

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.SetExchangeRate
import nft.freeport.processor.cms.InjectStrapiWiremock
import nft.freeport.processor.cms.WiremockStrapi
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class SetExchangeRateEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: SetExchangeRateEventProcessor

    @Test
    fun `processor is called -- updating request is sent to strapi`() {
        val event = SetExchangeRate(cereUnitsPerPenny = BigInteger.TEN)

        testSubject.process(event.wrapEvent())

        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-exchange-rate")).withRequestBody(
                equalToJson(buildJsonString {
                    put("cere_units_per_penny", 10)
                })
            )
        )
    }
}