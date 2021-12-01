package nft.freeport.processor.cms.nft

import nft.freeport.CURRENCY_TOKEN_ID
import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferSingle
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import nft.freeport.processor.freeport.nft.NftEntity
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferSingleEventProcessor(private val strapiService: StrapiService) :
    CmsEventProcessor<TransferSingle> {
    override val supportedClass = TransferSingle::class.java

    override fun process(eventData: SmartContractEventData<out TransferSingle>) = with(eventData.event) {
        if (from == ZERO_ADDRESS) {
            if (nftId != CURRENCY_TOKEN_ID) {
                strapiService.create(CmsConfig.Routes::nft, NftEntity(nftId, to, amount))
            }
        }
    }
}
