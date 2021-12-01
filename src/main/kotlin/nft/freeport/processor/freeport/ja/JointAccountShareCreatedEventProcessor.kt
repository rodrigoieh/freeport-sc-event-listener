package nft.freeport.processor.freeport.ja

import nft.freeport.listener.event.JointAccountShareCreated
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.FreeportEventProcessor
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class JointAccountShareCreatedEventProcessor : FreeportEventProcessor<JointAccountShareCreated> {
    override val supportedClass = JointAccountShareCreated::class.java

    @Transactional
    override fun process(eventData: SmartContractEventData<out JointAccountShareCreated>) = with(eventData.event) {
        JointAccountEntity(JointAccountEntityId(account, owner), fraction).persist()
    }
}
