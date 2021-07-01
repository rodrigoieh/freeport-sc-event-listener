package nft.davinci.wallet

import java.math.BigInteger
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Path("nfts")
class WalletResource(private val walletRepository: WalletRepository) {
    @GET
    @Path("{wallet}")
    suspend fun getWalletNfts(wallet: String): Map<String, BigInteger> {
        return walletRepository.getWalletNfts(wallet)
    }

    @GET
    @Path("{wallet}/{nftId}")
    suspend fun hasItem(wallet: String, nftId: String): Response {
        return if (walletRepository.hasItem(wallet, nftId)) {
            Response.ok()
        } else {
            Response.status(Response.Status.NOT_FOUND)
        }.build()
    }
}
