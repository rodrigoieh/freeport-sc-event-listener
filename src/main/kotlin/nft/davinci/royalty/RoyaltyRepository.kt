package nft.davinci.royalty

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import nft.davinci.event.RoyaltiesConfigured
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RoyaltyRepository(private val db: PgPool) {
    private companion object {
        private const val SQL_EXISTS = """
            SELECT 1 FROM api.nft_royalty
            WHERE nft_id = $1 AND sale_type = $2
        """
        private const val SQL_CREATE = """
            INSERT INTO api.nft_royalty (nft_id, sale_type, beneficiary, sale_cut, minimum_fee)
            VALUES ($1, $2, $3, $4, $5)
        """
        private const val SQL_UPDATE = """
            UPDATE api.nft_royalty 
            SET beneficiary = $3, sale_cut = $4, minimum_fee = $5
            WHERE nft_id = $1 AND sale_type = $2
        """
    }

    suspend fun save(event: RoyaltiesConfigured) {
        save(event.nftId, 1, event.primaryRoyaltyAccount, event.primaryRoyaltyCut, event.primaryRoyaltyMinimum)
        save(event.nftId, 2, event.secondaryRoyaltyAccount, event.secondaryRoyaltyCut, event.secondaryRoyaltyMinimum)
    }

    private suspend fun save(nftId: String, type: Int, beneficiary: String, cut: Int, minFee: BigInteger) {
        val exists = db.preparedQuery(SQL_EXISTS)
            .execute(Tuple.of(nftId, type))
            .awaitSuspending()
            .size() > 0
        val query = if (exists) SQL_UPDATE else SQL_CREATE
        SqlClientHelper.inTransactionUni(db) {
            db.preparedQuery(query)
                .execute(Tuple.of(nftId, type, beneficiary, cut, minFee))
        }.awaitSuspending()
    }
}
