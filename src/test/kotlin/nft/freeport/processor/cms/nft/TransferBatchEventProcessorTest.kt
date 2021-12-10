package nft.freeport.processor.cms.nft

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.TransferBatch
import nft.freeport.processor.cms.InjectStrapiWiremock
import nft.freeport.processor.cms.WiremockStrapi
import nft.freeport.processor.cms.stubGettingExistingStrapiNft
import nft.freeport.processor.cms.stubGettingStrapiWallet
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class TransferBatchEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: TransferBatchEventProcessor

    @Test
    fun `processor is called, both wallets exist -- both wallets quantities are updated`() {
        val event = TransferBatch(
            from = "0xFROM_TRANSER_BATCH",
            to = "0xTO_TRANSER_BATCH",
            operator = "OPERATOR_TRANSfER_SINGLE",
            ids = listOf("FIRST", "SECOND"),
            amounts = listOf(BigInteger.valueOf(1000), BigInteger.valueOf(10_000)),
        )

        event.ids.forEachIndexed { index, id ->
            val strapiNftId: Long = ((index + 1) * 100).toLong()
            wireMockServer.stubGettingExistingStrapiNft(smartContractNftId = id, strapiNftId = strapiNftId)
        }

        val firstNftSenderWalletStrapiId = 21343214131
        wireMockServer.stubGettingStrapiWallet(wallet = event.from, strapiNftId = 100) {
            put("id", firstNftSenderWalletStrapiId)
            put("quantity", 10_000)
        }
        val secondNftSenderWalletStrapiId = 12343241524
        wireMockServer.stubGettingStrapiWallet(wallet = event.from, strapiNftId = 200) {
            put("id", secondNftSenderWalletStrapiId)
            put("quantity", 100_000)
        }

        val firstNftReceiverWalletStrapiId = 259243892384
        wireMockServer.stubGettingStrapiWallet(wallet = event.to, strapiNftId = 100) {
            put("id", firstNftReceiverWalletStrapiId)
            put("quantity", 5_000)
        }
        val secondNftReceiverWalletStrapiId = 123403333
        wireMockServer.stubGettingStrapiWallet(wallet = event.to, strapiNftId = 200) {
            put("id", secondNftReceiverWalletStrapiId)
            put("quantity", 50_000)
        }


        testSubject.process(event.wrapEvent())

        // from sender
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-wallet-nfts/$firstNftSenderWalletStrapiId"))
                .withRequestBody(
                    equalToJson(buildJsonString {
                        put("quantity", 9000)
                    })
                )
        )
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-wallet-nfts/$secondNftSenderWalletStrapiId"))
                .withRequestBody(
                    equalToJson(buildJsonString {
                        put("quantity", 90_000)
                    })
                )
        )

        // to receiver
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-wallet-nfts/$firstNftReceiverWalletStrapiId"))
                .withRequestBody(
                    equalToJson(buildJsonString {
                        put("quantity", 6000)
                    })
                )
        )
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-wallet-nfts/$secondNftReceiverWalletStrapiId"))
                .withRequestBody(
                    equalToJson(buildJsonString {
                        put("quantity", 60_000)
                    })
                )
        )
    }
}