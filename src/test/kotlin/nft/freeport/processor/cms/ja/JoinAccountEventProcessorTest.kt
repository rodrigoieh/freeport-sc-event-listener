package nft.freeport.processor.cms.ja

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.listener.event.JointAccountShareCreated
import nft.freeport.processor.cms.InjectStrapiWiremock
import nft.freeport.processor.cms.WiremockStrapi
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockStrapi::class)
class JoinAccountEventProcessorTest {

    @field:InjectStrapiWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: JoinAccountEventProcessor

    @Test
    fun `processor is called -- the right request is sent to strapi`() {
        val event = JointAccountShareCreated(account = "account", owner = "owner", fraction = 42)

        testSubject.process(event.wrapEvent())

        val expectedRequestBody = """
                {
                  "owner": "${event.owner}",
                  "account": "${event.account}"
                }
            """.trimIndent()

        wireMockServer.verify(
            postRequestedFor(urlEqualTo("/creator-joint-accounts")).withRequestBody(
                equalToJson(expectedRequestBody)
            )
        )
    }
}