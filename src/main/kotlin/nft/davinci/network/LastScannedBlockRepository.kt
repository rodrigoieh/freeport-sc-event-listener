package nft.davinci.network

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class LastScannedBlockRepository(private val db: PgPool, private val networkConfig: NetworkConfig) {
    private companion object {
        private const val SQL_SELECT_LAST_BLOCK = """
            SELECT block_height FROM last_scanned_blocks
            WHERE network_id = $1 AND contract_address = $2
        """
        private const val SQL_INSERT_LAST_SCANNED_BLOCK = """
            INSERT INTO last_scanned_blocks (network_id, contract_address, block_height)
            VALUES ($1, $2, $3)
        """
        private const val SQL_UPDATE_LAST_SCANNED_BLOCK = """
            UPDATE last_scanned_blocks SET block_height = $3
            WHERE network_id = $1 AND contract_address = $2
        """
    }

    suspend fun getLastScannedBlock(): Long {
        val cachedBlock = db.preparedQuery(SQL_SELECT_LAST_BLOCK)
            .execute(Tuple.of(networkConfig.chainId(), networkConfig.contractAddress()))
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
        val params = Tuple.of(
            networkConfig.chainId(),
            networkConfig.contractAddress(),
            networkConfig.firstBlockNumber()
        )
        SqlClientHelper.inTransactionUni(db) {
            it.preparedQuery(SQL_INSERT_LAST_SCANNED_BLOCK).execute(params)
        }.awaitSuspending()
    }

    suspend fun updateLastScannedBlockNumber(blockNumber: Long) {
        val params = Tuple.of(
            networkConfig.chainId(),
            networkConfig.contractAddress(),
            blockNumber
        )
        SqlClientHelper.inTransactionUni(db) {
            it.preparedQuery(SQL_UPDATE_LAST_SCANNED_BLOCK).execute(params)
        }.awaitSuspending()
    }
}
