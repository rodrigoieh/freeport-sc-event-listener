package nft.davinci.db

import io.quarkus.runtime.StartupEvent
import org.flywaydb.core.Flyway
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class RunFlyway(private val cfg: DataSourceConfig) {
    private var run = false

    fun runMigration(@Observes e: StartupEvent) {
        Flyway.configure()
            .dataSource("jdbc:${cfg.url()}", cfg.username(), cfg.password())
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .outOfOrder(false)
            .load()
            .migrate()
        run = true
    }

    fun isCompleted() = run
}
