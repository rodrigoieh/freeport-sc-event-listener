package nft.davinci.nft

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.RowSet
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class WalletNftRepository(private val db: PgPool) {
    private companion object {
        private const val SQL_SELECT = """
            SELECT quantity FROM wallet_nft
            WHERE nft_id = $1 AND wallet = $2
        """
        private const val SQL_CREATE = """
            INSERT INTO wallet_nft (nft_id, wallet, quantity)
            VALUES ($1, $2, 0)
        """
        private const val SQL_UPDATE = """
            UPDATE wallet_nft SET quantity = $1 
            WHERE nft_id = $2 AND wallet = $3
        """
        private const val SQL_DELETE = "DELETE FROM wallet_nft WHERE nft_id = $1 AND wallet = $2"
    }

    suspend fun updateQuantity(wallet: String, nftId: String, delta: BigInteger) {
        SqlClientHelper.inTransactionUni(db) {
            getOrCreate(wallet, nftId)
                .flatMap {
                    val newQuantity = it + delta
                    if (newQuantity == BigInteger.ZERO) {
                        delete(wallet, nftId)
                    } else {
                        update(wallet, nftId, newQuantity)
                    }
                }
        }.awaitSuspending()
    }

    private fun getOrCreate(wallet: String, nftId: String): Uni<BigInteger> {
        return db.preparedQuery(SQL_SELECT)
            .execute(Tuple.of(nftId, wallet))
            .map {
                if (it.size() > 0) {
                    it.first().getBigDecimal(0).toBigInteger()
                } else {
                    create(wallet, nftId)
                    BigInteger.ZERO
                }
            }
    }

    private fun create(wallet: String, nftId: String): Uni<RowSet<Row>> {
        return db.preparedQuery(SQL_CREATE)
            .execute(Tuple.of(nftId, wallet))
    }

    private fun update(wallet: String, nftId: String, newQuantity: BigInteger): Uni<RowSet<Row>> {
        return db.preparedQuery(SQL_UPDATE)
            .execute(Tuple.of(newQuantity, nftId, wallet))
    }

    private fun delete(wallet: String, nftId: String): Uni<RowSet<Row>> {
        return db.preparedQuery(SQL_DELETE)
            .execute(Tuple.of(nftId, wallet))
    }
}
