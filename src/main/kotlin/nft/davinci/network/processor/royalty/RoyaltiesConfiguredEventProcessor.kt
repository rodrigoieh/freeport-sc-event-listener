package nft.davinci.network.processor.royalty

import nft.davinci.event.RoyaltiesConfigured
import nft.davinci.network.processor.EventProcessor
import nft.davinci.royalty.RoyaltyRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RoyaltiesConfiguredEventProcessor(private val royaltyRepository: RoyaltyRepository) :
    EventProcessor<RoyaltiesConfigured> {
    override val supportedClass = RoyaltiesConfigured::class.java

    override suspend fun process(event: RoyaltiesConfigured) {
        royaltyRepository.save(event)
    }
}
