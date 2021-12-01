package nft.freeport

import io.quarkus.test.common.QuarkusTestResource

@QuarkusTestResource(WiremockCovalent::class)
internal abstract class AbstractIntegrationTest