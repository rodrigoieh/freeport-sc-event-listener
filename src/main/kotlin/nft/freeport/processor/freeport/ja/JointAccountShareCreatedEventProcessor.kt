package nft.freeport.processor.freeport.ja

import nft.freeport.listener.event.SmartContractEventEntity
import nft.freeport.listener.event.JointAccountShareCreated
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class JointAccountShareCreatedEventProcessor : FreeportEventProcessor<JointAccountShareCreated> {
    override val supportedClass = JointAccountShareCreated::class.java

    @Transactional
    override fun process(event: JointAccountShareCreated, e: SmartContractEventEntity) {
        JointAccountEntity(JointAccountEntityId(event.account, event.owner), event.fraction).persist()
    }
}
