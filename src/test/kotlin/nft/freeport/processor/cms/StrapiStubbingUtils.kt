package nft.freeport.processor.cms

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.put
import nft.freeport.buildJsonArrayString

fun WireMockServer.stubForNftId(smartContractNftId: String, strapiNftId: Long) {
    stubFor(
        WireMock.get(WireMock.urlEqualTo("/creator-nfts?nft_id=$smartContractNftId"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(buildJsonArrayString {
                        addJsonObject {
                            put("id", strapiNftId)
                        }
                    })
            )
    )
}
