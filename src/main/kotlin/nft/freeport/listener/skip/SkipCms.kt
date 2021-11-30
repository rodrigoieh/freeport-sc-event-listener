package nft.freeport.listener.skip

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import nft.freeport.processor.cms.CmsConfig
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SkipCms(private val cmsConfig: CmsConfig) : Scheduled.SkipPredicate {
    override fun test(execution: ScheduledExecution): Boolean {
        return !cmsConfig.enabled()
    }
}