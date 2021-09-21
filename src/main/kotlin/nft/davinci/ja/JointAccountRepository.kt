package nft.davinci.ja

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JointAccountRepository(private val db: PgPool) {
    private companion object {
        private const val SQL_INSERT = """
            INSERT INTO api.joint_account (account, owner, fraction) VALUES ($1, $2, $3)
        """
    }

    suspend fun create(account: String, owner: String, fraction: Int) {
        SqlClientHelper.inTransactionUni(db) {
            db.preparedQuery(SQL_INSERT)
                .execute(Tuple.of(account, owner, fraction))
        }.awaitSuspending()
    }
}
