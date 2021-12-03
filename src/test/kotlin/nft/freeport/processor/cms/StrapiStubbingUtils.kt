package nft.freeport.processor.cms

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.put
import nft.freeport.buildJsonArrayString

fun WireMockServer.stubGettingStrapiNftId(smartContractNftId: String, strapiNftId: Long = STRAPI_NFT_ID) {
    stubFor(
        get(urlPathEqualTo("/creator-nfts"))
            .withQueryParam("nft_id", equalTo(smartContractNftId))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(buildJsonArrayString {
                        addJsonObject {
                            put("id", strapiNftId)
                        }
                    })
            )
    )
}

/**
 * The newest one by ends_at should be used
 */
fun WireMockServer.stubGettingStrapiAuctions(seller: String, auctionId: Long, strapiNftId: Long = STRAPI_NFT_ID) {
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

const val STRAPI_NFT_ID = 42L