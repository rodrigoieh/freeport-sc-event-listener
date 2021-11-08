package nft.freeport.network

import io.quarkus.test.junit.QuarkusTestProfile

class TestContractsEventsListenerConfig : QuarkusTestProfile {

    override fun getConfigOverrides(): Map<String, String> = mapOf(
        "covalent/mp-rest/url" to "http://localhost:3464/",
        "network.poll-interval" to "PT1S",
        "contracts.auction.first-block-number" to "41",
        "contracts.auction.address" to "0x0000"
    )

}