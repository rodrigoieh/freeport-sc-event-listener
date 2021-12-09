package nft.freeport.listener

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import kotlinx.serialization.json.*
import nft.freeport.buildJsonString
import java.time.Instant

private const val key = "ckey_103992e94cd94393beb35d1456d"

internal fun WireMockServer.stubGettingLatestBlock(block: Long) {
    stubFor(
        get(urlPathMatching("/v1/80001/block_v2/latest/"))
            .withQueryParam("key", equalTo(key))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(buildJsonString {
                        putJsonObject("data") {
                            putJsonArray("items") {
                                addJsonObject {
                                    put("height", block)
                                }
                            }
                        }
                        put("error_message", null as String?)
                        put("error", false)
                    })
            )
    )
}

internal fun WireMockServer.stubGettingEvents(
    contract: String,
    from: Long,
    to: Long,
    itemsActionBuilder: JsonArrayBuilder.() -> Unit
) {
    stubFor(
        get(urlPathEqualTo("/v1/80001/events/address/$contract/"))
            .withQueryParam("starting-block", equalTo(from.toString()))
            .withQueryParam("ending-block", equalTo(to.toString()))
            .withQueryParam("key", equalTo(key))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withBody(buildJsonString {
                        putJsonObject("data") {
                            putJsonArray("items") { itemsActionBuilder(this) }
                        }
                        put("error_message", null as String?)
                        put("error", false)
                    })
            )
    )
}

fun JsonArrayBuilder.generateOrderedEmptyEvents(
    startBlockNumber: Long,
    eventsPerBlock: Long,
    startEventNumber: Int,
    lastEventNumber: Int
) {
    (startEventNumber..lastEventNumber).forEach {
        addJsonObject {
            val blockNumber = startBlockNumber + (it / eventsPerBlock)

            put("block_signed_at", Instant.now().toString())
            put("block_height", blockNumber)
            put("tx_hash", "0x$blockNumber")
            putJsonArray("raw_log_topics") {
                add("ONLY_TEST_TOPIC")
            }
            put("raw_log_data", it)
            putJsonObject("decoded") {
                put("name", it)
                putJsonArray("params") {
                    addJsonObject {
                        put("name", "TEST_ONLY_EMPTY_PARAM_NAME")
                        put("value", it)
                    }
                }
            }
        }
    }
}
