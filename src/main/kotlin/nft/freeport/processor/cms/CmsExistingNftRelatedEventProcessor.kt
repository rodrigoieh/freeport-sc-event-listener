package nft.freeport.processor.cms

import nft.freeport.listener.event.NftRelatedEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.strapi.StrapiService
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

/**
 * Can be used when a processor works with [NftRelatedEvent] and wants to skip events with invalid [NftRelatedEvent.nftId] reference.
 */
@ApplicationScoped
interface CmsExistingNftRelatedEventProcessor<T> :
    CmsEventProcessor<T> where T : SmartContractEvent, T : NftRelatedEvent {

    val strapiService: StrapiService

    fun process(eventData: SmartContractEventData<out T>, nftId: Long, minter: String)

    override fun process(eventData: SmartContractEventData<out T>) {
        val nft = strapiService.findOne(CmsConfig.Routes::nft, mapOf("nft_id" to eventData.event.nftId)) ?: run {
            log.warn(
                "NFT {} doesn't exist in strapi. Event {} is skipped.",
                eventData.event.nftId,
                eventData.event::class.simpleName,
            )

            return
        }

        process(eventData = eventData, nftId = nft.getLong("id"), minter = nft.getString("minter"))
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}