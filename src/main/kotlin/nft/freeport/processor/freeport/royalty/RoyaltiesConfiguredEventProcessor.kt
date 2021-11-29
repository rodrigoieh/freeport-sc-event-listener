package nft.freeport.processor.freeport.royalty

import nft.freeport.listener.event.RoyaltiesConfigured
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class RoyaltiesConfiguredEventProcessor : FreeportEventProcessor<RoyaltiesConfigured> {
    override val supportedClass = RoyaltiesConfigured::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out RoyaltiesConfigured>) = with(eventData.event) {
        save(nftId, 1, primaryRoyaltyAccount, primaryRoyaltyCut, primaryRoyaltyMinimum)
        save(nftId, 2, secondaryRoyaltyAccount, secondaryRoyaltyCut, secondaryRoyaltyMinimum)
    }

    private fun save(nftId: String, type: Int, beneficiary: String, cut: Int, minFee: BigInteger) {
        val id = RoyaltyEntityId(nftId, type)
        val entity = RoyaltyEntity.findById(id) ?: RoyaltyEntity(id, beneficiary, cut, minFee)
        entity.apply {
            this.beneficiary = beneficiary
            this.saleCut = cut
            this.minimumFee = minFee
        }.persist()
    }
}
