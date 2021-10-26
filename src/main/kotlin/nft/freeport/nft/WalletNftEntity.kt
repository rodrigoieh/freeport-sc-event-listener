package nft.freeport.nft

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "wallet_nft", schema = "api")
class WalletNftEntity(
    @EmbeddedId
    val id: WalletNftEntityId,

    @Column
    var quantity: BigInteger,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<WalletNftEntity, WalletNftEntityId>
}