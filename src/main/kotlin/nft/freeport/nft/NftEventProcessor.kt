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
    @Transactional
    fun onNftEvent(event: NftEvent, blockSignedAt: String) {
        ddcService.sendNftEvent(event, blockSignedAt)
        when (event) {
            is NftMinted -> onNftMinted(event)
            is NftTransferred -> onNftTransferred(event)
        }
    }

    private fun onNftMinted(event: NftMinted) {
        NftEntity(NftEntityId(event.nftId, event.minter), event.quantity).persist()
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
