package nft.freeport.nft

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class NftEntityId(
    @Column(name = "nft_id")
    val nftId: String,

    @Column
    val minter: String,
) : Serializable