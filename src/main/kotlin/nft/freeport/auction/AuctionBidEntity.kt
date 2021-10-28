package nft.freeport.auction

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "auction_bid", schema = "api")
class AuctionBidEntity(
    @Id
    @SequenceGenerator(
        name = "auctionBidSeq",
        sequenceName = "auction_bid_id_seq",
        schema = "api",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auctionBidSeq")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id")
    val auction: AuctionEntity,

    @Column
    val buyer: String,

    @Column
    val price: BigInteger,

    @Column
    val timestamp: Instant,
) : PanacheEntityBase