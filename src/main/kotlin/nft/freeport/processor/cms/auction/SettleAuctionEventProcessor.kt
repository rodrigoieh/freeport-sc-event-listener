package nft.freeport.processor.cms.auction

import io.vertx.core.json.JsonObject
import nft.freeport.listener.event.SettleAuction
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SettleAuctionEventProcessor(private val strapiService: StrapiService) : CmsEventProcessor<SettleAuction> {
    private val log = LoggerFactory.getLogger(javaClass)
    override val supportedClass = SettleAuction::class.java

    override fun process(eventData: SmartContractEventData<out SettleAuction>) = with(eventData) {
        val nft = strapiService.findId(CmsConfig.Routes::nft, mapOf("nft_id" to event.nftId))
        if (nft == null) {
            log.warn("Unable to find NFT with id {} in CMS", event.nftId)
            return@with
        }
        val auctions = strapiService.findAll(
            CmsConfig.Routes::auction,
            mapOf(
                "nft_id" to nft,
                "seller" to event.seller
            )
        )
        if (auctions.isEmpty) {
            log.warn("Unable to find auction for NFT {} and seller {}", event.nftId, event.seller)
            return@with
        }
        val auctionId = auctions.map { it as JsonObject }
            .sortedByDescending { it.getInstant("ends_at") }
            .first()
            .getLong("id")
        strapiService.update(
            CmsConfig.Routes::auction,
            auctionId,
            mapOf(
                "buyer" to event.buyer,
                "price" to event.price,
                // We don't have any other information in event, so consider that auction is settled once SC transaction was confirmed.
                "ends_at" to Instant.parse(rawEvent.blockSignedAt),
                "is_settled" to true
            )
        )
    }
}