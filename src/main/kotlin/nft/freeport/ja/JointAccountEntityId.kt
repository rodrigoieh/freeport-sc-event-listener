package nft.freeport.ja

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class JointAccountEntityId(
    @Column
    val account: String,

    @Column
    val owner: String
) : Serializable