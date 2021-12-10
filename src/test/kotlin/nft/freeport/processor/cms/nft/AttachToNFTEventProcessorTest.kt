package nft.freeport.processor.cms.nft

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.AttachToNFT
import nft.freeport.processor.cms.*
import nft.freeport.processor.cms.stubGettingExistingStrapiNft
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class AttachToNFTEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: AttachToNFTEventProcessor

    @Test
    fun `processor is called, sender == minter from strapi -- creation request is sent to strapi`() {
        val event = AttachToNFT(nftId = "attach_nft_id", sender = "0xATTACH_SENDER", cid = "0xATTACH_CID")

        wireMockServer.stubGettingExistingStrapiNft(smartContractNftId = event.nftId) {
            put("minter", event.sender)
        }
        wireMockServer.stubEntityCreation(entityPath = "/creator-nft-cids")

        testSubject.process(event.wrapEvent())

        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-nft-cids")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", STRAPI_NFT_ID)
                    put("sender", event.sender)
                    put("cid", event.cid)
                })
            )
        )
    }

    @Test
    fun `processor is called, sender != minter from strapi -- request is not sent`() {
        val event = AttachToNFT(nftId = "attach_nft_id", sender = "0xATTACH_SENDER", cid = "0xATTACH_CID")

        wireMockServer.stubGettingExistingStrapiNft(smartContractNftId = event.nftId) {
            put("minter", "0xANOTHER_MINTER")
        }

        testSubject.process(event.wrapEvent())

        wireMockServer.verify(
            0,
            postRequestedFor(urlPathEqualTo("/creator-nft-cids"))
        )
    }
}