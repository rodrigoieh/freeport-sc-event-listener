package nft.freeport.processor.freeport.auction

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.panache.common.Sort
import java.math.BigInteger
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "auction")
class AuctionEntity(
    @Id
    @SequenceGenerator(
        name = "auctionSeq",
        sequenceName = "auction_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auctionSeq")
    val id: Long?,

    @Column
    val seller: String,

    @Column
    var buyer: String,

    @Column(name = "nft_id")
    val nftId: String,

    @Column
    var price: BigInteger,

    @Column(name = "ends_at")
    var endsAt: Instant,

    @Column(name = "is_settled")
    var isSettled: Boolean
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<AuctionEntity, Long> {
        fun findActive(seller: String, nftId: String): AuctionEntity {
            return find("seller = ?1 AND nftId = ?2", Sort.descending("endsAt"), seller, nftId)
                .firstResult()
                .let(::requireNotNull)
        }
    }
}