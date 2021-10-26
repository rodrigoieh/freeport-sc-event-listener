package nft.freeport.price

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.*

@Entity
@Table(name = "take_offer", schema = "api")
class TakeOfferEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api.take_offer_id_seq")
    val id: Long?,

    @Column
    val buyer: String,

    @Column
    val seller: String,

    @Column
    val nftId: String,

    @Column
    val price: BigInteger,

    @Column
    val amount: BigInteger
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<TakeOfferEntity, Long>
}