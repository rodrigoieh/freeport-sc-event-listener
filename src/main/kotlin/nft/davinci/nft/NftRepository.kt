package nft.davinci.nft

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NftRepository(private val db: PgPool) {
    private companion object {
        private const val SQL_CREATE = """
            INSERT INTO api.nft (nft_id, minter, supply)
            VALUES ($1, $2, $3)
        """
    }

    suspend fun create(nftId: String, minter: String, supply: BigInteger) {
        SqlClientHelper.inTransactionUni(db) {
            db.preparedQuery(SQL_CREATE)
                .execute(Tuple.of(nftId, minter, supply))
        }.awaitSuspending()
    }
}
