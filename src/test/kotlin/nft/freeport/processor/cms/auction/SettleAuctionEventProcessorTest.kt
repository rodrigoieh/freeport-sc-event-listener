package nft.freeport.processor.cms.auction

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.SettleAuction
import nft.freeport.processor.cms.InjectStrapiWiremock
import nft.freeport.processor.cms.WiremockStrapi
import nft.freeport.processor.cms.stubGettingStrapiAuctions
import nft.freeport.processor.cms.stubGettingExistingStrapiNft
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class SettleAuctionEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: SettleAuctionEventProcessor

    @Test
    // todo why do we update buyer price and ends_at fields?
    fun `processor is called -- updating is_settle auction field request is sent to strapi`() {
        val event = SettleAuction(
            nftId = "settle_auction_nft_id",
            seller = "0xSTART_AUCTION_SELLER",
            price = BigInteger.TEN,
            buyer = "0xSTART_AUCTION_BUYER",
        )
        wireMockServer.stubGettingExistingStrapiNft(smartContractNftId = event.nftId)

        val auctionId = 1L
        wireMockServer.stubGettingStrapiAuctions(seller = event.seller, auctionId = auctionId)

        val blockSignedAt = "2021-12-01T12:00:00Z"
        testSubject.process(event.wrapEvent(blockSignedAt = blockSignedAt))

        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-auctions/$auctionId")).withRequestBody(
                equalToJson(buildJsonString {
                    put("buyer", event.buyer)
                    put("price", event.price.toInt())
                    // todo is it the right logic? ends_at os block signed datetime?
                    put("ends_at", blockSignedAt)
                    put("is_settled", true)
                })
            )
        )
    }
}