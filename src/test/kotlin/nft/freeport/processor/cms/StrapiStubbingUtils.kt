package nft.freeport.processor.cms

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.put
import nft.freeport.buildJsonArrayString
import nft.freeport.buildJsonString

internal fun WireMockServer.stubGettingStrapiWallet(
    wallet: String,
    strapiNftId: Long = STRAPI_NFT_ID,
    fieldsBuilderAction: (JsonObjectBuilder.() -> Unit)? = null
) {
    stubFor(
        get(urlPathEqualTo("/creator-wallet-nfts"))
            .withQueryParam("nft_id", equalTo(strapiNftId.toString()))
            .withQueryParam("wallet", equalTo(wallet))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(buildJsonArrayString { fieldsBuilderAction?.let { addJsonObject(it) } })
            )
    )
}


internal fun WireMockServer.stubGettingStrapiNft(
    smartContractNftId: String,
    strapiNftId: Long = STRAPI_NFT_ID,
    minter: String = STRAPI_NFT_MINTER,
    additionalFieldsBuilderAction: JsonObjectBuilder.() -> Unit = { }
) {
    stubFor(
        get(urlPathEqualTo("/creator-nfts"))
            .withQueryParam("nft_id", equalTo(smartContractNftId))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(buildJsonArrayString {
                        addJsonObject {
                            put("id", strapiNftId)
                            put("minter", minter)
                            additionalFieldsBuilderAction()
                        }
                    })
            )
    )
}

/**
 * The newest one by ends_at should be used
 */
internal fun WireMockServer.stubGettingStrapiAuctions(
    seller: String,
    auctionId: Long,
    strapiNftId: Long = STRAPI_NFT_ID
) {
    stubFor(
        get(urlPathEqualTo("/creator-auctions"))
            .withQueryParam("nft_id", equalTo(strapiNftId.toString()))
            .withQueryParam("seller", equalTo(seller))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(buildJsonArrayString {
                        addJsonObject {
                            put("id", Long.MIN_VALUE)
                            put("ends_at", "2020-01-01T12:00:00Z")
                        }
                        addJsonObject {
                            put("id", auctionId)
                            put("ends_at", "2021-01-01T12:00:00Z")
                        }
                    })
            )
    )
}

internal fun WireMockServer.stubEntityCreation(entityPath: String, id: Long = 42) {
    stubFor(
        post(urlPathEqualTo(entityPath))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(
                        buildJsonString {
                            put("id", id)
                        }
                    )
            )
    )
}

const val STRAPI_NFT_ID = 42L
const val STRAPI_NFT_MINTER = "0x00000000000042"