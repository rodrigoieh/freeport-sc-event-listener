package nft.freeport.processor.cms.nft

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.serialization.json.put
import nft.freeport.buildJsonString
import nft.freeport.listener.event.TransferSingle
import nft.freeport.processor.cms.*
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class TransferSingleEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: TransferSingleEventProcessor

    @Test
    fun `processor is called, both wallets exist -- both wallets quantities are updated`() {
        val event = TransferSingle(
            nftId = "transfer_single_nft_id",
            from = "0xFROM_TRANSER_SINGLE",
            to = "0xTO_TRANSER_SINGLE",
            operator = "OPERATOR_TRANSfER_SINGLE",
            amount = BigInteger.valueOf(25)
        )
        wireMockServer.stubGettingStrapiNft(smartContractNftId = event.nftId)

        val senderWalletStrapiId = 1927321
        wireMockServer.stubGettingStrapiWallet(wallet = event.from) {
            put("id", senderWalletStrapiId)
            put("quantity", 1000)
        }

        val receiverWalletStrapId = 424132
        wireMockServer.stubGettingStrapiWallet(wallet = event.to) {
            put("id", receiverWalletStrapId)
            put("quantity", 500)
        }

        testSubject.process(event.wrapEvent())

        // from sender
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-wallet-nfts/$senderWalletStrapiId")).withRequestBody(
                equalToJson(buildJsonString {
                    put("quantity", 975)
                })
            )
        )
        // to receiver
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-wallet-nfts/$receiverWalletStrapId")).withRequestBody(
                equalToJson(buildJsonString {
                    put("quantity", 525)
                })
            )
        )
    }

    @Test
    fun `processor is called, sender is ZERO_ADDRESS, nftId is cere token -- only receiver updation request is sent`() {
        val event = TransferSingle(
            // cere token
            nftId = "0",
            from = "0x0000000000000000000000000000000000000000",
            to = "0xTO_TRANSER_SINGLE_CERE_TOKEN",
            operator = "OPERATOR_TRANSfER_SINGLE_CERE_TOKEN",
            amount = BigInteger.valueOf(50_000)
        )
        wireMockServer.stubGettingStrapiNft(smartContractNftId = event.nftId)
        val receiverWalletStrapId = 54562534543
        wireMockServer.stubGettingStrapiWallet(wallet = event.to) {
            put("id", receiverWalletStrapId)
            put("quantity", 500)
        }

        testSubject.process(event.wrapEvent())

        // to receiver
        wireMockServer.verify(
            putRequestedFor(urlPathEqualTo("/creator-wallet-nfts/$receiverWalletStrapId")).withRequestBody(
                equalToJson(buildJsonString {
                    put("quantity", 50_500)
                })
            )
        )
    }

    @Test
    fun `processor is called, sender is ZERO_ADDRESS -- nft creation request is sent (receiver == minter)`() {
        val event = TransferSingle(
            nftId = "transfer_single_nft_id_zero_address",
            from = "0x0000000000000000000000000000000000000000",
            to = "0xTO_TRANSER_SINGLE_ZERO_ADDRESS",
            operator = "OPERATOR_TRANSfER_SINGLE_ZERO_ADDRESS",
            amount = BigInteger.valueOf(5000)
        )
        wireMockServer.stubGettingStrapiNft(smartContractNftId = event.nftId)
        wireMockServer.stubGettingStrapiWallet(wallet = event.to) {
            put("id", 456)
            put("quantity", 500)
        }
        wireMockServer.stubEntityCreation(entityPath = "/creator-nfts")

        testSubject.process(event.wrapEvent())

        // nft is created
        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-nfts")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", event.nftId)
                    put("minter", event.to)
                    put("supply", 5000)
                })
            )
        )
    }

    @Test
    fun `processor is called, both wallets don't exist -- two wallets creation requests are sent`() {
        val event = TransferSingle(
            nftId = "transfer_single_nft_id_wallets_missed",
            from = "0xFROM_TRANSER_SINGLE_WALLETS_MISSED",
            to = "0xTO_TRANSER_SINGLE_WALLETS_MISSED",
            operator = "OPERATOR_TRANSfER_SINGLE_WALLETS_MISSED",
            amount = BigInteger.valueOf(25)
        )
        wireMockServer.stubGettingStrapiNft(smartContractNftId = event.nftId)

        // no wallets, just empty arrays
        wireMockServer.stubGettingStrapiWallet(wallet = event.from)
        wireMockServer.stubGettingStrapiWallet(wallet = event.to)
        wireMockServer.stubEntityCreation(entityPath = "/creator-wallet-nfts")

        testSubject.process(event.wrapEvent())

        // todo is it expected? sender is missed, negative balance?
        // from sender
        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-wallet-nfts")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", STRAPI_NFT_ID)
                    put("wallet", event.from)
                    put("quantity", -25)
                })
            )
        )
        // to receiver
        wireMockServer.verify(
            postRequestedFor(urlPathEqualTo("/creator-wallet-nfts")).withRequestBody(
                equalToJson(buildJsonString {
                    put("nft_id", STRAPI_NFT_ID)
                    put("wallet", event.to)
                    put("quantity", 25)
                })
            )
        )
    }
}