package nft.davinci.network.converter.raw

import nft.davinci.event.TakeOffer
import nft.davinci.network.NetworkConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TakeOfferConverter(
    networkConfig: NetworkConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<TakeOffer> {
    private val topic = networkConfig.eventTopics().getValue(TakeOffer::class.java.simpleName)

    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == topic
    }

    override fun convert(source: ContractEvent): TakeOffer {
        TODO("Not yet implemented")
    }
}