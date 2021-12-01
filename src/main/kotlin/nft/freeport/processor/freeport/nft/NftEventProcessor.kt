package nft.freeport.processor.freeport.nft

import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NftEventProcessor {
    fun updateQuantity(address: String, nftId: String, delta: BigInteger) {
        val walletId = WalletNftEntityId(nftId, address)
        val wallet = WalletNftEntity.findById(walletId)

        if (wallet == null) {
            WalletNftEntity(walletId, delta).persist()
        } else {
            wallet.quantity = wallet.quantity + delta
            wallet.persist()
        }
    }
}
