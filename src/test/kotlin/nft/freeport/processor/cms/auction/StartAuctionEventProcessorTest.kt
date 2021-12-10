package nft.freeport.processor.cms.auction

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.StartAuction
import nft.freeport.processor.cms.*
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class StartAuctionEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: StartAuctionEventProcessor

    @Test
    fun `processor is called -- auction creation request is sent to strapi`() {
        val event = StartAuction(
            nftId = "start_auction_nft_id",
            seller = "0xSTART_AUCTION_SELLER",
            price = BigInteger.TEN,
            closeTimeSec = BigInteger.valueOf(1609502400)
        )
        wireMockServer.stubGettingExistingStrapiNft(smartContractNftId = event.nftId)
        wireMockServer.stubEntityCreation(entityPath = "/creator-auctions")

        testSubject.process(event.wrapEvent())

        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-auctions")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", STRAPI_NFT_ID)
                    put("seller", event.seller)
                    put("buyer", "0x0000000000000000000000000000000000000000")
                    // price should be increased with 10%
                    put("price", BigInteger.valueOf(11))
                    put("ends_at", "2021-01-01T12:00:00Z")
                    put("is_settled", false)
                })
            )
        )
    }
}