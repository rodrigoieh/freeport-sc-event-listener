package nft.freeport.processor.cms.auction

import io.vertx.core.json.JsonObject
import nft.freeport.listener.event.BidOnAuction
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BidOnAuctionEventProcessor(private val strapiService: StrapiService) : CmsEventProcessor<BidOnAuction> {
    private val log = LoggerFactory.getLogger(javaClass)
    override val supportedClass = BidOnAuction::class.java

    override fun process(eventData: SmartContractEventData<out BidOnAuction>) = with(eventData) {
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
                "ends_at" to Instant.ofEpochSecond(event.closeTimeSec.longValueExact())
            )
        )
        val bid = AuctionBid(
            auctionId = auctionId,
            buyer = event.buyer,
            price = event.price,
            timestamp = Instant.parse(rawEvent.blockSignedAt)
        )
        strapiService.create(CmsConfig.Routes::auctionBid, bid)
    }
}