package nft.freeport.processor.freeport.ja

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.*

@Entity
@Table(name = "joint_account")
class JointAccountEntity(
    @EmbeddedId
    val id: JointAccountEntityId,

    @Column
    val fraction: Int
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<JointAccountEntity, JointAccountEntityId>
}