package nft.freeport.processor.cms.price

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.MakeOffer
import nft.freeport.processor.cms.InjectStrapiWiremock
import nft.freeport.processor.cms.STRAPI_NFT_ID
import nft.freeport.processor.cms.WiremockStrapi
import nft.freeport.processor.cms.stubGettingStrapiNftId
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class MakeOfferEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: MakeOfferEventProcessor

    @Test
    fun `processor is called -- creation request is sent to strapi, nft_id from strapi is used`() {
        val event = MakeOffer(nftId = "make_offer_nft_id", seller = "0xSELLER", price = BigInteger.TEN)

        wireMockServer.stubGettingStrapiNftId(smartContractNftId = event.nftId)

        testSubject.process(event.wrapEvent())

        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-make-offers")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", STRAPI_NFT_ID)
                    put("seller", event.seller)
                    put("price", 10)
                })
            )
        )
    }
}