package nft.freeport.processor.cms

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType

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
                    equalToJson(
                        """
                    {
                        "email": "api-user",
                        "password": "api-password"
                    }
                """.trimIndent()
                    )
                )
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody(
                            """
                    {
                        "data": {
                            "token": "$token",
                            "user": {
                                "id": 1,
                                "firstname": "admin",
                                "lastname": "admin",
                                "username": null,
                                "email": "admin@admin.admin",
                                "registrationToken": null,
                                "isActive": true,
                                "blocked": null,
                                "preferedLanguage": null,
                                "roles": [
                                    {
                                        "id": 1,
                                        "name": "Super Admin",
                                        "description": "Super Admins can access and manage all features and settings.",
                                        "code": "strapi-super-admin"
                                    }
                                ]
                            }
                        }
                    }
                """.trimIndent()
                        )
                )
        );

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