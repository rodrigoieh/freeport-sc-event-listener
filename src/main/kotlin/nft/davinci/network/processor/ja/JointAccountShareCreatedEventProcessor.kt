package nft.davinci.network.processor.ja

import nft.davinci.event.JointAccountShareCreated
import nft.davinci.network.processor.EventProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JointAccountShareCreatedEventProcessor : EventProcessor<JointAccountShareCreated> {
    override val supportedClass = JointAccountShareCreated::class.java

    override suspend fun process(event: JointAccountShareCreated) {
        TODO("Not yet implemented")
    }
}
