package nft.freeport.processor.cms.royalty

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.RoyaltiesConfigured
import nft.freeport.processor.cms.InjectStrapiWiremock
import nft.freeport.processor.cms.WiremockStrapi
import nft.freeport.processor.cms.stubForNftId
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class RoyaltiesConfiguredEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: RoyaltiesConfiguredEventProcessor

    @Test
    fun `processor is called -- two right requests are sent to strapi, internal strapi id is used as nft_id`() {
        val event = RoyaltiesConfigured(
            nftId = "123456789",
            primaryRoyaltyAccount = "0x001",
            primaryRoyaltyCut = 100,
            primaryRoyaltyMinimum = BigInteger.valueOf(99),

            secondaryRoyaltyAccount = "0x002",
            secondaryRoyaltyCut = 200,
            secondaryRoyaltyMinimum = BigInteger.valueOf(199),
        )

        wireMockServer.stubForNftId(smartContractNftId = event.nftId, strapiNftId = 1001L)

        testSubject.process(event.wrapEvent())

        wireMockServer.verify(
            postRequestedFor(urlEqualTo("/creator-nft-roaylties")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", 1001L)
                    put("sale_type", 1)
                    put("beneficiary", event.primaryRoyaltyAccount)
                    put("sale_cut", event.primaryRoyaltyCut)
                    put("minimum_fee", event.primaryRoyaltyMinimum)
                })
            )
        )
        wireMockServer.verify(
            postRequestedFor(urlEqualTo("/creator-nft-roaylties")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", 1001L)
                    put("sale_type", 2)
                    put("beneficiary", event.secondaryRoyaltyAccount)
                    put("sale_cut", event.secondaryRoyaltyCut)
                    put("minimum_fee", event.secondaryRoyaltyMinimum)
                })
            )
        )
    }
}