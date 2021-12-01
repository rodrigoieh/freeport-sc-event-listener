package nft.freeport.listener.skip

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import nft.freeport.processor.ddc.DdcConfig
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SkipDdc(private val ddcConfig: DdcConfig) : Scheduled.SkipPredicate {
    override fun test(execution: ScheduledExecution): Boolean {
        return !ddcConfig.enabled()
    }
}