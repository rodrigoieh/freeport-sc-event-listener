package nft.freeport.processor.cms

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import nft.freeport.buildJsonString

class WiremockStrapi : QuarkusTestResourceLifecycleManager {

    private val port = 8090
    private val wireMockServer: WireMockServer = WireMockServer(port)
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaWF0IjoxNjM4NDY1MjA2LCJleHAiOjE2NDEwNTcyMDZ9.gFLrbjgOM-MnLOIR884YoNUN66bvv_j4zmtPTnIgAjs"

    override fun start(): MutableMap<String, String> {
        wireMockServer.start()

        wireMockServer.stubFor(
            post(urlEqualTo("/admin/login"))
                .withRequestBody(
                    // credentials from configs
                    equalToJson(buildJsonString {
                        put("email", "api-user")
                        put("password", "api-password")
                    })
                )
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody(
                            // real response
                            buildJsonString {
                                putJsonObject("data") {
                                    put("token", token)
                                    putJsonObject("user") {
                                        put("id", 1)
                                        put("firstname", "admin")
                                        put("lastname", "admin")
                                        put("username", null as String?)
                                        put("email", "admin@admin.admin")
                                        put("registrationToken", null as String?)
                                        put("isActive", true)
                                        put("blocked", null as String?)
                                        put("preferedLanguage", null as String?)
                                        putJsonArray("roles") {
                                            addJsonObject {
                                                put("id", 1)
                                                put("name", "Super Admin")
                                                put(
                                                    "description",
                                                    "Super Admins can access and manage all features and settings."
                                                )
                                                put("code", "strapi-super-admin")
                                            }
                                        }
                                    }
                                }
                            }
                        )
                )
        )

        return mutableMapOf("cms.base-url" to "http://localhost:${wireMockServer.port()}");
    }

    override fun stop() {
        wireMockServer.stop()
    }

    override fun inject(testInjector: TestInjector) {
        testInjector.injectIntoFields(
            wireMockServer, AnnotatedAndMatchesType(
                InjectStrapiWiremock::class.java,
                WireMockServer::class.java
            )
        )
    }

}