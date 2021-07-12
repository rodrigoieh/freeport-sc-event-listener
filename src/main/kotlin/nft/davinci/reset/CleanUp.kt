package nft.davinci.reset

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CleanUp(private val db: PgPool) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val query = sequenceOf("nft", "wallet_nft", "wallet_nft", "joint_account")
        .map { "TRUNCATE TABLE $it;" }
        .joinToString(separator = "\n")

    suspend fun truncateDb() {
        log.info("Truncating database")
        SqlClientHelper.inTransactionUni(db) {
            db.query(query).execute()
        }.awaitSuspending()
    }
}
