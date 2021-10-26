package nft.freeport.royalty

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class RoyaltyEntityId(
    @Column(name = "nft_id")
    val nftId: String,

    @Column(name = "sale_type")
    val saleType: Int
) : Serializable