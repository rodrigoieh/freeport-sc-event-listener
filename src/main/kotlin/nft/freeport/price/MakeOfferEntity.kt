package nft.freeport.price

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "make_offer", schema = "api")
class MakeOfferEntity(
    @EmbeddedId
    val id: MakeOfferEntityId,

    @Column(name = "price")
    var priceInCereTokens: BigInteger
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<MakeOfferEntity, MakeOfferEntityId>
}