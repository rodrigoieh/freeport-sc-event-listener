package nft.freeport

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import nft.freeport.covalent.dto.Block
import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.covalent.dto.CovalentResponse
import nft.freeport.covalent.dto.CovalentResponseData
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

internal class WiremockCovalent : QuarkusTestResourceLifecycleManager {
    private val objectMapper = jacksonObjectMapper()
    private lateinit var wireMockServer: WireMockServer

    override fun start(): Map<String, String> {
        wireMockServer = WireMockServer()
        wireMockServer.start()

        stubFor(
            get("/v1/80001/block_v2/latest/")
                .willReturn(
                    aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(
                            CovalentResponse<Block>(
                                data = CovalentResponseData(listOf(Block(21202116))),
                                error = false,
                                errorMessage = null
                            ).let(objectMapper::writeValueAsString)
                        )
                )
        )

        stubFor(
            get(urlMatching("/v1/80001/events/address/.+"))
                .willReturn(
                    aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(
                            CovalentResponse<ContractEvent>(
                                data = CovalentResponseData(listOf()),
                                error = false,
                                errorMessage = null
                            ).let(objectMapper::writeValueAsString)
                        )
                )
        )

        return mapOf("covalent/mp-rest/url" to wireMockServer.baseUrl())
    }

    override fun stop() {
        wireMockServer.stop()
    }
}