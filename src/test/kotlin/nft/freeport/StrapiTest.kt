package nft.freeport

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.strapi.StrapiService
import nft.freeport.processor.freeport.nft.NftEntity
import nft.freeport.processor.freeport.nft.WalletNftEntity
import nft.freeport.processor.freeport.nft.WalletNftEntityId
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
class StrapiTest {
    @Inject
    lateinit var strapiService: StrapiService

    @Test
    fun test() {
        strapiService.create(
           CmsConfig.Routes::wallet,
            WalletNftEntity(WalletNftEntityId("eee", "ddd"), BigInteger.TEN)
        )
    }
}