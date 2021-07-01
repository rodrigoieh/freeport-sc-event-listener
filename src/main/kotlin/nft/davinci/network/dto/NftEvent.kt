package nft.davinci.network.dto

sealed class NftEvent(
    open val operator: String,
    open val nftId: String,
    open val quantity: Long
) {
    abstract fun eventType(): String
}

data class NftMinted(
    override val operator: String,
    val minter: String,
    override val nftId: String,
    override val quantity: Long
) : NftEvent(operator, nftId, quantity) {
    override fun eventType() = "NFT_MINTED"
}

data class NftTransferred(
    override val operator: String,
    val from: String,
    val to: String,
    override val nftId: String,
    override val quantity: Long
) : NftEvent(operator, nftId, quantity) {
    override fun eventType() = "NFT_TRANSFERRED"
}

data class NftBurned(
    override val operator: String,
    val from: String,
    override val nftId: String,
    override val quantity: Long
) : NftEvent(operator, nftId, quantity) {
    override fun eventType() = "NFT_BURNED"
}
