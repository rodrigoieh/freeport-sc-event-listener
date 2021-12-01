package nft.freeport.processor.cms.ja

import nft.freeport.listener.event.JointAccountShareCreated
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JoinAccountEventProcessor(
    private val strapiService: StrapiService,
) : CmsEventProcessor<JointAccountShareCreated> {
    override val supportedClass = JointAccountShareCreated::class.java

    override fun process(eventData: SmartContractEventData<out JointAccountShareCreated>) = with(eventData.event) {
        strapiService.create(
            route = CmsConfig.Routes::jointAccount,
            payload = JointAccount(owner = owner, account = account)
        )
    }
}
