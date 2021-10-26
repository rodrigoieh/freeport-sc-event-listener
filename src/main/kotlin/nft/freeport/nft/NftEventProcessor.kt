package nft.freeport.nft

import nft.freeport.ddc.DdcService
import nft.freeport.event.NftEvent
import nft.freeport.event.NftMinted
import nft.freeport.event.NftTransferred
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class NftEventProcessor(private val ddcService: DdcService) {
    private companion object {
        private const val ZERO_NFT_ID = "0"
    }

    @Transactional
    fun onNftEvent(event: NftEvent, blockSignedAt: String, txHash: String) {
        ddcService.sendNftEvent(event, blockSignedAt, txHash)
        when (event) {
            is NftMinted -> onNftMinted(event)
            is NftTransferred -> onNftTransferred(event)
        }
    }

    private fun onNftMinted(event: NftMinted) {
        // special case - we might have 2 events with tokens NFT (0)
        if (event.nftId == ZERO_NFT_ID && NftEntity.findById(ZERO_NFT_ID) != null) {
            return
        }
        NftEntity(event.nftId, event.minter, event.quantity).persist()
        updateQuantity(event.minter, event.nftId, event.quantity)
    }

    private fun onNftTransferred(event: NftTransferred) {
        updateQuantity(event.from, event.nftId, -event.quantity)
        updateQuantity(event.to, event.nftId, event.quantity)
    }

    private fun updateQuantity(address: String, nftId: String, delta: BigInteger) {
        val walletId = WalletNftEntityId(nftId, address)
        val wallet = WalletNftEntity.findById(walletId)
        if (wallet == null) {
            WalletNftEntity(walletId, delta).persist()
        } else {
            val newQuantity = wallet.quantity + delta
            if (newQuantity == BigInteger.ZERO) {
                wallet.delete()
            } else {
                wallet.quantity = newQuantity
                wallet.persist()
            }
        }
    }
}
