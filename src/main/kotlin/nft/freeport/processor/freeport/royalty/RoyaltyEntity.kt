package nft.freeport.processor.freeport.royalty

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "nft_royalty")
class RoyaltyEntity(
    @EmbeddedId
    val id: RoyaltyEntityId,

    @Column
    var beneficiary: String,

    @Column(name = "sale_cut")
    var saleCut: Int,

    @Column(name = "minimum_fee")
    var minimumFee: BigInteger,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<RoyaltyEntity, RoyaltyEntityId>
}