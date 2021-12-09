package nft.freeport.processor.cms.price

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.TakeOffer
import nft.freeport.processor.cms.InjectStrapiWiremock
import nft.freeport.processor.cms.STRAPI_NFT_ID
import nft.freeport.processor.cms.WiremockStrapi
import nft.freeport.processor.cms.stubGettingStrapiNft
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class TakeOfferEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: TakeOfferEventProcessor

    @Test
    fun `processor is called -- creation request is sent to strapi, nft_id from strapi is used`() {
        val event = TakeOffer(
            nftId = "take_offer_nft_id",
            seller = "0x0SELLER",
            buyer = "0xBUYER",
            price = BigInteger.TEN,
            amount = BigInteger.ONE
        )

        wireMockServer.stubGettingStrapiNft(smartContractNftId = event.nftId)

        testSubject.process(event.wrapEvent())

        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-take-offers")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", STRAPI_NFT_ID)
                    put("buyer", event.buyer)
                    put("seller", event.seller)
                    put("price", event.price.toInt())
                    put("amount", event.amount.toInt())
                })
            )
        )
    }
}