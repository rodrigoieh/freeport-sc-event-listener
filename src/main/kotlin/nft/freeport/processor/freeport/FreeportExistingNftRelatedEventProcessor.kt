package nft.freeport.processor.freeport

import nft.freeport.listener.event.NftRelatedEvent
import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.nft.NftEntity
import org.slf4j.LoggerFactory

/**
 * Can be used when a processor works with [NftRelatedEvent] and wants to skip events with invalid [NftRelatedEvent.nftId] reference.
 */
interface FreeportExistingNftRelatedEventProcessor<T> : FreeportEventProcessor<T> where T : SmartContractEvent,
                                                                                        T : NftRelatedEvent {
    override val supportedClass: Class<T>

    fun process(eventData: SmartContractEventData<out T>, nft: NftEntity)

    override fun process(eventData: SmartContractEventData<out T>) {
        val nft = NftEntity.findById(eventData.event.nftId) ?: run {
            log.warn(
                "Received {} event for non-existing NFT {}. Skip.",
                eventData.event::class.simpleName,
                eventData.event.nftId,
            )

            return
        }

        process(eventData = eventData, nft = nft)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}