package nft.davinci.network.processor.nft

import nft.davinci.ddc.DdcService
import nft.davinci.event.NftMinted
import nft.davinci.event.NftTransferred
import nft.davinci.event.TransferBatch
import nft.davinci.nft.NftRepository
import nft.davinci.nft.WalletNftRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferBatchEventProcessor(
    ddcService: DdcService,
    nftRepository: NftRepository,
    walletNftRepository: WalletNftRepository
) : AbstractNftEventProcessor<TransferBatch>(ddcService, nftRepository, walletNftRepository) {
    override val supportedClass = TransferBatch::class.java

    override suspend fun process(event: TransferBatch) {
        val (operator, from, to, ids, amounts) = event
        ids.indices.forEach { i ->
            val nftEvent = if (from == ZERO_ADDRESS) {
                NftMinted(operator, to, ids[i], amounts[i])
            } else {
                NftTransferred(operator, from, to, ids[i], amounts[i])
            }
            onNftEvent(nftEvent)
        }
    }
}
