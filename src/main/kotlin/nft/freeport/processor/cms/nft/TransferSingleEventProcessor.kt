package nft.freeport.processor.cms.nft

import nft.freeport.CURRENCY_TOKEN_ID
import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferSingle
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferSingleEventProcessor(
    private val strapiService: StrapiService,
    private val nftEventProcessor: NftEventProcessor
) : CmsEventProcessor<TransferSingle> {
    override val supportedClass = TransferSingle::class.java

    override fun process(eventData: SmartContractEventData<out TransferSingle>) = with(eventData.event) {
        if (from == ZERO_ADDRESS) {
            if (nftId != CURRENCY_TOKEN_ID) {
                strapiService.create(CmsConfig.Routes::nft, Nft(nftId = nftId, minter = to, supply = amount))
            }
        } else {
            nftEventProcessor.updateQuantity(from, nftId, -amount)
        }
        nftEventProcessor.updateQuantity(to, nftId, amount)
    }
}
