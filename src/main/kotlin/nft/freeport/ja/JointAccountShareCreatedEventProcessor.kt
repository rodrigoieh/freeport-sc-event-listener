package nft.freeport.ja

import nft.freeport.event.JointAccountShareCreated
import nft.freeport.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class JointAccountShareCreatedEventProcessor : EventProcessor<JointAccountShareCreated> {
    override val supportedClass = JointAccountShareCreated::class.java

    @Transactional
    override fun process(event: JointAccountShareCreated) {
        JointAccountEntity(JointAccountEntityId(event.account, event.owner), event.fraction).persist()
    }
}
