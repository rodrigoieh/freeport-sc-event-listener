package nft.freeport.processor.cms.nft

import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NftEventProcessor(private val strapiService: StrapiService) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun updateQuantity(address: String, nftId: String, delta: BigInteger) {
        val nft = strapiService.findId(CmsConfig.Routes::nft, mapOf("nft_id" to nftId))
        if (nft == null) {
            log.warn("Unable to find NFT with id {} in CMS", nftId)
            return
        }

        val wallet = strapiService.findOne(CmsConfig.Routes::wallet, mapOf("nft_id" to nft, "wallet" to address))

        if (wallet == null) {
            strapiService.create(CmsConfig.Routes::wallet, WalletNft(nft, address, delta))
        } else {
            val newQuantity = wallet.getString("quantity").toBigInteger() + delta
            strapiService.update(CmsConfig.Routes::wallet, wallet.getLong("id"), mapOf("quantity" to newQuantity))
        }
    }
}
