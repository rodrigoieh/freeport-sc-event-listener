package nft.davinci.reset

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CleanUp(private val db: PgPool) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val tables = listOf("nft", "wallet_nft", "last_scanned_block", "joint_account")

    suspend fun truncateDb() {
        log.info("Truncating database")
        tables.forEach { table ->
            log.info("Truncating table $table")
            SqlClientHelper.inTransactionUni(db) {
                it.query("TRUNCATE TABLE $table").execute()
            }.awaitSuspending()
        }
    }
}
