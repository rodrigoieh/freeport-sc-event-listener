package nft.freeport.processor.cms.auction

import io.vertx.core.json.JsonObject
import nft.freeport.listener.event.SettleAuction
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsExistingNftRelatedEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SettleAuctionEventProcessor(override val strapiService: StrapiService) :
    CmsExistingNftRelatedEventProcessor<SettleAuction> {
    private val log = LoggerFactory.getLogger(javaClass)
    override val supportedClass = SettleAuction::class.java

    override fun process(eventData: SmartContractEventData<out SettleAuction>, nftId: Long, minter: String) {
        val auctions = strapiService.findAll(
            CmsConfig.Routes::auction,
            mapOf(
                "nft_id" to nftId,
                "seller" to eventData.event.seller
            )
        )
        if (auctions.isEmpty) {
            log.warn("Unable to find auction for NFT {} and seller {}", eventData.event.nftId, eventData.event.seller)
            return
        }
        val auctionId = auctions.map { it as JsonObject }
            .sortedByDescending { it.getInstant("ends_at") }
            .first()
            .getLong("id")

        strapiService.update(
            CmsConfig.Routes::auction,
            auctionId,
            mapOf(
                "buyer" to eventData.event.buyer,
                "price" to eventData.event.price,
                "ends_at" to Instant.parse(eventData.rawEvent.blockSignedAt),
                "is_settled" to true
            )
        )
    }
}