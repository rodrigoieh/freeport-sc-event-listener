package nft.davinci.price

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlClientHelper
import io.vertx.mutiny.sqlclient.Tuple
import nft.davinci.event.MakeOffer
import nft.davinci.event.TakeOffer
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PriceRepository(private val db: PgPool) {
    private companion object {
        private const val SQL_CREATE_OR_UPDATE_MAKE_OFFER = """
            INSERT INTO make_offer (seller, nftId, price) 
            VALUES ($1, $2, $3) 
            ON CONFLICT ON CONSTRAINT make_offer_pk DO UPDATE 
            SET price = $3
        """
        private const val SQL_CREATE_TAKE_OFFER = """
            INSERT INTO take_offer (buyer, seller, nftId, price, amount)
            VALUES ($1, $2, $3, $4, $5)
        """
    }

    suspend fun createOrUpdateMakeOffer(event: MakeOffer) {
        SqlClientHelper.inTransactionUni(db) {
            db.preparedQuery(SQL_CREATE_OR_UPDATE_MAKE_OFFER)
                .execute(Tuple.of(event.seller, event.nftId, event.price))
        }.awaitSuspending()
    }

    suspend fun createTakeOffer(event: TakeOffer) {
        SqlClientHelper.inTransactionUni(db) {
            db.preparedQuery(SQL_CREATE_TAKE_OFFER)
                .execute(Tuple.of(event.buyer, event.seller, event.nftId, event.price, event.amount))
        }.awaitSuspending()
    }
}