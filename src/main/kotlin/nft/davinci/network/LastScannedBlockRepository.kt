package nft.davinci.network

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class LastScannedBlockRepository(private val db: PgPool, private val networkConfig: NetworkConfig) {
    private companion object {
        private const val SQL_SELECT = "SELECT block_height FROM last_scanned_blocks"
        private const val SQL_INSERT = "INSERT INTO last_scanned_blocks (block_height) VALUES ($1)"
        private const val SQL_UPDATE = "UPDATE last_scanned_blocks SET block_height = $1"
    }

    suspend fun getLastScannedBlock(): Long {
        val cachedBlock = db.preparedQuery(SQL_SELECT)
            .execute()
            .awaitSuspending()
            .firstOrNull()
            ?.getLong(0)
        return if (cachedBlock == null) {
            initLastScannedBlockNumber()
            networkConfig.firstBlockNumber()
        } else {
            cachedBlock
        }
    }

    suspend fun initLastScannedBlockNumber() {
        SqlClientHelper.inTransactionUni(db) {
            it.preparedQuery(SQL_INSERT)
                .execute(Tuple.of(networkConfig.firstBlockNumber()))
        }.awaitSuspending()
    }

    suspend fun updateLastScannedBlockNumber(blockNumber: Long) {
        SqlClientHelper.inTransactionUni(db) {
            it.preparedQuery(SQL_UPDATE).execute(Tuple.of(blockNumber))
        }.awaitSuspending()
    }
}
