package nft.freeport.price

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class MakeOfferEntityId(
    @Column
    val seller: String,

    @Column
    val nftId: String
) : Serializable