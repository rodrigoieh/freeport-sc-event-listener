package nft.freeport.processor.freeport.nft

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.*

@Entity
@Table(name = "nft_cid")
class NftCidEntity(
    @Id
    @SequenceGenerator(
        name = "nftCidSeq",
        sequenceName = "nft_cid_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nftCidSeq")
    val id: Long?,

    @Column(name = "nft_id")
    val nftId: String,

    @Column
    val sender: String,

    @Column
    val cid: String
) : PanacheEntityBase {
    companion object : PanacheCompanion<NftCidEntity>
}