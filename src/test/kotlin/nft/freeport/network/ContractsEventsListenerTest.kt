package nft.freeport.network

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import io.quarkus.test.junit.mockito.InjectMock
import io.quarkus.test.junit.mockito.InjectSpy
import nft.freeport.network.config.ContractsConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.inject.Inject

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestProfile(TestContractsEventsListenerConfig::class)
class ContractsEventsListenerTest {

    private val server: WireMockServer = WireMockServer(3464)

    @BeforeAll
    internal fun startMockServer(): Unit = server.start()

    @AfterAll
    internal fun stopMockServer(): Unit = server.stop()

    @InjectSpy
    internal lateinit var contractsEventsListener: ContractsEventsListener

    // just to prevent runs
    @InjectMock
    internal lateinit var contractsEventScheduler: ContractsEventListenerHelper

    @Inject
    internal lateinit var contractsConfig: ContractsConfig

    private val contracts by lazy { contractsConfig.contracts().values }

    @Test
    fun `sync triggered but the last block hasn't changed yet -- no new processing`() {
        val testContact = "0x0000"
        server.stubFor(
            get(urlMatching("/v1/.*/block_v2/latest/.*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "data": {
                                "updated_at": "2021-11-05T16:29:30.968164306Z",
                                "items": [
                                  {
                                    "signed_at": "2021-11-05T16:29:30Z",
                                    "height": 42
                                  }
                                ]
                              },
                              "error": false,
                              "error_message": null,
                              "error_code": null
                            }
                        """.trimIndent()
                        )
                )
        )

        server.stubFor(
            get(urlMatching("/v1/.*/events/address/$testContact.*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "data": {
                                "updated_at": "2021-11-05T16:35:16.679420368Z",
                                "items": [
                                  {
                                    "block_signed_at": "2021-09-06T18:23:31Z",
                                    "block_height": 42,
                                    "tx_hash": "0x913744f84e114b59cb1d156a98eedde8ed51d45424137ca531c8bac6c96c4547",
                                    "raw_log_topics": [
                                      "0x1d5de90e7c5b244ac5797698b15fe80a92524d933dafd79e001daf844555fb1c"
                                    ],
                                    "raw_log_data": "0x000000000000000000000000000000000000000000000000000000003b9aca00",
                                    "decoded": null
                                  }
                                ]
                              },
                              "error": false,
                              "error_message": null,
                              "error_code": null
                            }
        """.trimIndent()
                        )
                )
        )

        val contractConfig = contracts.find { it.address() == testContact }
            ?: error("contact 0x0000 should present in this test")

        contractsEventsListener.init(contractConfig)

        // request for the events should be sent, last scan block from memory is 41, last available from network is 42
        contractsEventsListener.sync(testContact)
        server.verify(1, getRequestedFor(urlMatching("/v1/.*/events/address/$testContact.*")))

        // no more call, last scan from memory == last available from network, processing should be skipped
        contractsEventsListener.sync(testContact)
        server.verify(1, getRequestedFor(urlMatching("/v1/.*/events/address/$testContact.*")))
    }
}