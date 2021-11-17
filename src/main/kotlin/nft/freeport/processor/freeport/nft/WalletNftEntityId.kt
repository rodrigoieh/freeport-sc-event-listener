package nft.freeport.processor.freeport.nft

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class WalletNftEntityId(
    @Column(name = "nft_id")
    val nftId: String,

    @Column
    val wallet: String
) : Serializable