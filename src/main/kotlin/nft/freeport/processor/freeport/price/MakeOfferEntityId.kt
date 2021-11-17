package nft.freeport.processor.freeport.price

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class MakeOfferEntityId(
    @Column
    val seller: String,

    @Column(name = "nft_id")
    val nftId: String
) : Serializable