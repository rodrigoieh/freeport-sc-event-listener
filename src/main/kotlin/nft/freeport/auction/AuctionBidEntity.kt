package nft.freeport.auction

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "auction_bid")
class AuctionBidEntity(
    @Id
    @SequenceGenerator(
        name = "auctionBidSeq",
        sequenceName = "auction_bid_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auctionBidSeq")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "auction_id")
    val auction: AuctionEntity,

    @Column
    val buyer: String,

    @Column
    val price: BigInteger,

    @Column
    val timestamp: Instant,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<AuctionBidEntity, Long>
}