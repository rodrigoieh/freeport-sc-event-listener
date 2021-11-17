package nft.freeport.processor.freeport.nft

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.*

@Entity
@Table(name = "nft")
class NftEntity(
    @Id
    @Column(name = "nft_id")
    val nftId: String,

    @Column
    val minter: String,

    @Column
    val supply: BigInteger,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<NftEntity, String>
}