package nft.davinci.network

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import nft.davinci.network.config.ContractConfig
import nft.davinci.network.config.ContractsConfig
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class LastScannedBlockRepository(private val db: PgPool, contractsConfig: ContractsConfig) {
    private companion object {
        private const val SQL_SELECT = "SELECT block_height FROM api.last_scanned_block WHERE contract = $1"
        private const val SQL_INSERT = "INSERT INTO api.last_scanned_block (contract, block_height) VALUES ($1, $2)"
        private const val SQL_UPDATE = "UPDATE api.last_scanned_block SET block_height = $1  WHERE contract = $2"
    }

    suspend fun getLastScannedBlock(contract: ContractConfig): Long {
        val cachedBlock = db.preparedQuery(SQL_SELECT)
            .execute(Tuple.of(contract.address()))
            .awaitSuspending()
            .firstOrNull()
            ?.getLong(0)
        return if (cachedBlock == null) {
            initLastScannedBlockNumber(contract)
            contract.firstBlockNumber()
        } else {
            cachedBlock
        }
    }

    suspend fun initLastScannedBlockNumber(contract: ContractConfig) {
        SqlClientHelper.inTransactionUni(db) {
            it.preparedQuery(SQL_INSERT)
                .execute(Tuple.of(contract.address(), contract.firstBlockNumber()))
        }.awaitSuspending()
    }

    suspend fun updateLastScannedBlockNumber(contract: String, blockNumber: Long) {
        SqlClientHelper.inTransactionUni(db) {
            it.preparedQuery(SQL_UPDATE).execute(Tuple.of(blockNumber, contract))
        }.awaitSuspending()
    }
}
