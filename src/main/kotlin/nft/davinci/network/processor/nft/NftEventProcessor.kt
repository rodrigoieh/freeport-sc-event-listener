package nft.davinci.network.processor.nft

import kotlinx.coroutines.coroutineScope
import nft.davinci.ddc.DdcService
import nft.davinci.event.NftEvent
import nft.davinci.event.NftMinted
import nft.davinci.event.NftTransferred
import nft.davinci.nft.NftRepository
import nft.davinci.nft.WalletNftRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NftEventProcessor(
    private val ddcService: DdcService,
    private val nftRepository: NftRepository,
    private val walletNftRepository: WalletNftRepository
) {
    suspend fun onNftEvent(event: NftEvent, blockSignedAt: String, txHash: String) = coroutineScope {
        ddcService.sendNftEvent(event, blockSignedAt, txHash)
        when (event) {
            is NftMinted -> onNftMinted(event)
            is NftTransferred -> onNftTransferred(event)
        }
    }

    private suspend fun onNftMinted(event: NftMinted) = coroutineScope {
        nftRepository.create(event.nftId, event.minter, event.quantity)
        walletNftRepository.updateQuantity(event.minter, event.nftId, event.quantity)
    }

    private suspend fun onNftTransferred(event: NftTransferred) = coroutineScope {
        walletNftRepository.updateQuantity(event.from, event.nftId, -event.quantity)
        walletNftRepository.updateQuantity(event.to, event.nftId, event.quantity)
    }
}
