package nft.davinci.network.processor.ja

import nft.davinci.event.JointAccountShareCreated
import nft.davinci.ja.JointAccountRepository
import nft.davinci.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JointAccountShareCreatedEventProcessor(private val jointAccountRepository: JointAccountRepository) :
    EventProcessor<JointAccountShareCreated> {
    override val supportedClass = JointAccountShareCreated::class.java

    override suspend fun process(event: JointAccountShareCreated) {
        jointAccountRepository.create(event.account, event.owner, event.fraction)
    }
}
