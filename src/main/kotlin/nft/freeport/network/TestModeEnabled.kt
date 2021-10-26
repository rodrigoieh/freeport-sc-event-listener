package nft.freeport.network

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TestModeEnabled(@ConfigProperty(name = "quarkus.profile") private val profile: String) : Scheduled.SkipPredicate {
    override fun test(execution: ScheduledExecution): Boolean {
        return profile == "test"
    }
}