package nft.freeport.price

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.*

@Entity
@Table(name = "take_offer", schema = "api")
class TakeOfferEntity(
    @Id
    @SequenceGenerator(
        name = "takeOfferSeq",
        sequenceName = "take_offer_id_seq",
        schema = "api",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "takeOfferSeq")
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