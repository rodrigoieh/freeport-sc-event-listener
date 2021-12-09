package nft.freeport.listener

import com.github.tomakehurst.wiremock.WireMockServer
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType

internal class WiremockCovalent : QuarkusTestResourceLifecycleManager {

    private val port = 8091
    private val wireMockServer: WireMockServer = WireMockServer(port)

    override fun start(): Map<String, String> {
        wireMockServer.start()

        return mapOf("covalent/mp-rest/url" to wireMockServer.baseUrl())
    }

    override fun stop() {
        wireMockServer.stop()
    }

    override fun inject(testInjector: QuarkusTestResourceLifecycleManager.TestInjector) {
        testInjector.injectIntoFields(
            wireMockServer, AnnotatedAndMatchesType(
                InjectCovalentWiremock::class.java,
                WireMockServer::class.java
            )
        )
    }
}