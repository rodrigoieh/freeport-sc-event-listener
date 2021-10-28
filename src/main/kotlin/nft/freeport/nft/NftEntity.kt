package nft.freeport.nft

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.*

@Entity
@Table(name = "nft", schema = "api")
class NftEntity(
    @EmbeddedId
    val id: NftEntityId,

    @Column
    val supply: BigInteger,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<NftEntity, NftEntityId>
}