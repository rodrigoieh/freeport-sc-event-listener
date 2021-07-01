package nft.davinci.wallet

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.RowSet
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import nft.davinci.network.NetworkConfig
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class WalletRepository(private val db: PgPool, networkConfig: NetworkConfig) {
    private companion object {
        private const val SQL_SELECT_BY_WALLET = """
            SELECT nft_id, quantity 
            FROM wallet_nfts
            WHERE wallet = $1 AND network_id = $2 AND contract_address = $3
        """
        private const val SQL_SELECT_BY_WALLET_AND_NFT_ID = """
            SELECT id, quantity 
            FROM wallet_nfts
            WHERE wallet = $1 AND nft_id = $2 AND network_id = $3 AND contract_address = $4
        """
        private const val SQL_CREATE = """
            INSERT INTO wallet_nfts (network_id, contract_address, wallet, nft_id, quantity)
            VALUES ($1, $2, $3, $4, 0)
            RETURNING id
        """
        private const val SQL_UPDATE = """
            UPDATE wallet_nfts SET quantity = $1 WHERE id = $2
        """
        private const val SQL_DELETE = """
            DELETE FROM wallet_nfts WHERE id = $1
        """
    }

    private val networkId = networkConfig.chainId()
    private val contractAddress = networkConfig.contractAddress()

    suspend fun getWalletNfts(wallet: String): Map<String, BigInteger> {
        return db.preparedQuery(SQL_SELECT_BY_WALLET)
            .execute(Tuple.of(wallet, networkId, contractAddress))
            .awaitSuspending()
            .asSequence()
            .map { it.getString(0) to it.getBigDecimal(1).toBigInteger() }
            .toMap()
    }

    suspend fun hasItem(wallet: String, nftId: String): Boolean {
        return db.preparedQuery(SQL_SELECT_BY_WALLET_AND_NFT_ID)
            .execute(Tuple.of(wallet, nftId, networkId, contractAddress))
            .awaitSuspending()
            .size() > 0
    }

    suspend fun updateQuantity(wallet: String, nftId: String, delta: BigInteger) {
        SqlClientHelper.inTransactionUni(db) {
            getOrCreate(wallet, nftId)
                .flatMap {
                    val newQuantity = it.quantity + delta
                    if (newQuantity == BigInteger.ZERO) {
                        delete(it.id)
                    } else {
                        update(it.id, newQuantity)
                    }
                }
        }.awaitSuspending()
    }

    private fun getOrCreate(wallet: String, nftId: String): Uni<WalletNft> {
        return db.preparedQuery(SQL_SELECT_BY_WALLET_AND_NFT_ID)
            .execute(Tuple.of(wallet, nftId, networkId, contractAddress))
            .flatMap {
                if (it.size() > 0) {
                    val row = it.first()
                    Uni.createFrom().item(WalletNft(row.getLong(0), wallet, nftId, row.getBigDecimal(1).toBigInteger()))
                } else {
                    create(wallet, nftId)
                }
            }
    }

    private fun create(wallet: String, nftId: String): Uni<WalletNft> {
        return db.preparedQuery(SQL_CREATE)
            .execute(Tuple.of(networkId, contractAddress, wallet, nftId))
            .map {
                val id = it.first().getLong(0)
                WalletNft(id, wallet, nftId)
            }
    }

    private fun update(id: Long, newQuantity: BigInteger): Uni<RowSet<Row>> {
        return db.preparedQuery(SQL_UPDATE).execute(Tuple.of(newQuantity, id))
    }

    private fun delete(id: Long): Uni<RowSet<Row>> {
        return db.preparedQuery(SQL_DELETE).execute(Tuple.of(id))
    }
}
