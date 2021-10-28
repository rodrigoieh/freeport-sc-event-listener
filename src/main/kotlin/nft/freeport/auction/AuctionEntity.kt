package nft.freeport.auction

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "auction", schema = "api")
class AuctionEntity(
    @Id
    @SequenceGenerator(
        name = "auctionSeq",
        sequenceName = "auction_id_seq",
        schema = "api",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auctionSeq")
    val id: Long?,

    @Column
    val seller: String,

    @Column
    val buyer: String,

    @Column(name = "nft_id")
    val nftId: String,

    @Column
    val price: BigInteger,

    @Column(name = "ends_at")
    val endsAt: Instant
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<AuctionEntity, Long>
}