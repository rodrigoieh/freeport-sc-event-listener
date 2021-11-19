package nft.freeport.processor.freeport.royalty

import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.RoyaltiesConfigured
import nft.freeport.processor.freeport.FreeportEventProcessor
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class RoyaltiesConfiguredEventProcessor : FreeportEventProcessor<RoyaltiesConfigured> {
    override val supportedClass = RoyaltiesConfigured::class.java

    @Transactional
    override fun process(event: RoyaltiesConfigured, e: SmartContractEventEntity) {
        save(event.nftId, 1, event.primaryRoyaltyAccount, event.primaryRoyaltyCut, event.primaryRoyaltyMinimum)
        save(event.nftId, 2, event.secondaryRoyaltyAccount, event.secondaryRoyaltyCut, event.secondaryRoyaltyMinimum)
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
