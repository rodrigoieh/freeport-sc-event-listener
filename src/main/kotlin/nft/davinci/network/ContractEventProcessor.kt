package nft.davinci.network

import io.vertx.pgclient.PgException
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
        // TODO sometimes we might get duplicate minted event. Need to research why it happens
        runCatching {
            nftRepository.create(event.nftId, event.minter, event.quantity)
        }.fold(
            { walletNftRepository.updateQuantity(event.minter, event.nftId, event.quantity) },
            {
                if (it is PgException && it.code == "23505") {
                    log.warn("Duplicate {} event. Ignored.", event.eventType())
                } else {
                    throw it
                }
            }
        )
    }

    private suspend fun onNftTransferred(event: NftTransferred) = coroutineScope {
        walletNftRepository.updateQuantity(event.from, event.nftId, -event.quantity)
        walletNftRepository.updateQuantity(event.to, event.nftId, event.quantity)
    }

    private suspend fun onNftBurned(event: NftBurned) = coroutineScope {
        walletNftRepository.updateQuantity(event.from, event.nftId, -event.quantity)
    }
}
