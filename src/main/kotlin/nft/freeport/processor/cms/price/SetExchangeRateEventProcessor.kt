package nft.freeport.processor.cms.price

import nft.freeport.listener.event.SetExchangeRate
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.CmsEventProcessor
import nft.freeport.processor.cms.strapi.StrapiService
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SetExchangeRateEventProcessor(private val strapiService: StrapiService) : CmsEventProcessor<SetExchangeRate> {
    override val supportedClass = SetExchangeRate::class.java

    override fun process(eventData: SmartContractEventData<out SetExchangeRate>) {
        val rate = ExchangeRate(eventData.event.cereUnitsPerPenny)
        strapiService.update(CmsConfig.Routes::exchangeRate, rate)
    }
}