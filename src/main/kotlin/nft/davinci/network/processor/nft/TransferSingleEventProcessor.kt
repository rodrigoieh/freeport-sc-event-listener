package nft.davinci.network.processor.nft

import nft.davinci.ddc.DdcService
import nft.davinci.event.NftMinted
import nft.davinci.event.NftTransferred
import nft.davinci.event.TransferSingle
import nft.davinci.nft.NftRepository
import nft.davinci.nft.WalletNftRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferSingleEventProcessor(
    ddcService: DdcService,
    nftRepository: NftRepository,
    walletNftRepository: WalletNftRepository
) : AbstractNftEventProcessor<TransferSingle>(ddcService, nftRepository, walletNftRepository) {
    override val supportedClass = TransferSingle::class.java

    override suspend fun process(event: TransferSingle) {
        val (operator, from, to, id, amount) = event
        val nftEvent = if (from == ZERO_ADDRESS) {
            NftMinted(operator, to, id, amount)
        } else {
            NftTransferred(operator, from, to, id, amount)
        }
        onNftEvent(nftEvent)
    }
}
