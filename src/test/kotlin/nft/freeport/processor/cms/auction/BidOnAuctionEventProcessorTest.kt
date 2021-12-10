package nft.freeport.processor.cms.auction

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.BidOnAuction
import nft.freeport.processor.cms.*
import nft.freeport.processor.cms.stubGettingStrapiAuctions
import nft.freeport.processor.cms.stubGettingExistingStrapiNft
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class BidOnAuctionEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: BidOnAuctionEventProcessor

    @Test
    fun `processor is called -- updating auction request(buyer,price,ends_at) is sent to strapi, bid creation request is sent`() {
        val event = BidOnAuction(
            nftId = "bid_on_auction_nft_id",
            seller = "0xBID_ON_AUCTION_SELLER",
            price = BigInteger.valueOf(15),
            buyer = "0xBID_ON_AUCTION_BUYER",
            closeTimeSec = BigInteger.valueOf(1609502400)
        )
        wireMockServer.stubGettingExistingStrapiNft(smartContractNftId = event.nftId)

        val auctionId = 1L
        wireMockServer.stubGettingStrapiAuctions(seller = event.seller, auctionId = auctionId)
        wireMockServer.stubEntityCreation(entityPath = "/creator-auction-bids")

        val blockSignedAt = "2021-12-01T12:00:00Z"
        testSubject.process(event.wrapEvent(blockSignedAt = blockSignedAt))

        // auction is updated
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-auctions/$auctionId")).withRequestBody(
                equalToJson(buildJsonString {
                    put("buyer", event.buyer)
                    put("price", event.price.toInt())
                    put("ends_at", "2021-01-01T12:00:00Z")
                })
            )
        )

        // bid is created
        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-auction-bids")).withRequestBody(
                equalToJson(buildJsonString {
                    put("auction_id", auctionId)
                    put("price", event.price.toInt())
                    put("buyer", event.buyer)
                    put("timestamp", blockSignedAt)
                })
            )
        )
    }
}