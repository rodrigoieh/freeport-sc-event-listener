package nft.davinci.network

import kotlinx.coroutines.coroutineScope
import nft.davinci.ddc.DdcService
import nft.davinci.network.dto.DecodedContractEvent
import nft.davinci.network.dto.NftBurned
import nft.davinci.network.dto.NftMinted
import nft.davinci.network.dto.NftTransferred
import nft.davinci.nft.NftRepository
import nft.davinci.nft.WalletNftRepository
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ContractEventProcessor(
    private val ddcService: DdcService,
    private val nftRepository: NftRepository,
    private val walletNftRepository: WalletNftRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun process(event: DecodedContractEvent) = coroutineScope {
        val nftEvent = event.toNftEvent() ?: return@coroutineScope
        log.info("Received {} event", nftEvent.eventType())
        ddcService.sendNftEvent(nftEvent)
        when (nftEvent) {
            is NftMinted -> onNftMinted(nftEvent)
            is NftTransferred -> onNftTransferred(nftEvent)
            is NftBurned -> onNftBurned(nftEvent)
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

    private suspend fun onNftBurned(event: NftBurned) = coroutineScope {
        walletNftRepository.updateQuantity(event.from, event.nftId, -event.quantity)
    }
}
