package nft.freeport.listener

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "last_scanned_block")
class LastScannedBlockEntity(
    @Id
    val contract: String,

    @Column(name = "block_height")
    var blockHeight: Long
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<LastScannedBlockEntity, String>
}