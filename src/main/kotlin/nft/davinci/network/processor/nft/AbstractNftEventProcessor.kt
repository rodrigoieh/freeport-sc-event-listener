package nft.davinci.network.processor.nft

import kotlinx.coroutines.coroutineScope
import nft.davinci.ddc.DdcService
import nft.davinci.event.NftEvent
import nft.davinci.event.NftMinted
import nft.davinci.event.NftTransferred
import nft.davinci.event.SmartContractEvent
import nft.davinci.network.processor.EventProcessor
import nft.davinci.nft.NftRepository
import nft.davinci.nft.WalletNftRepository
import org.slf4j.LoggerFactory

abstract class AbstractNftEventProcessor<T : SmartContractEvent>(
    private val ddcService: DdcService,
    private val nftRepository: NftRepository,
    private val walletNftRepository: WalletNftRepository
) : EventProcessor<T> {
    private val log = LoggerFactory.getLogger(javaClass)

    protected companion object {
        const val ZERO_ADDRESS = "0x0000000000000000000000000000000000000000"
    }

    protected suspend fun onNftEvent(event: NftEvent) = coroutineScope {
        log.info("Received {} event", event.eventType())
        ddcService.sendNftEvent(event)
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
