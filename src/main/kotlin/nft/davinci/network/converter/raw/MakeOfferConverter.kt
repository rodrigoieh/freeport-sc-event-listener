package nft.davinci.network.converter.raw

import nft.davinci.event.MakeOffer
import nft.davinci.event.RoyaltiesConfigured
import nft.davinci.network.NetworkConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MakeOfferConverter(
    networkConfig: NetworkConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<MakeOffer> {
    private val topic = networkConfig.eventTopics().getValue(MakeOffer::class.java.simpleName)

    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == topic
    }

    override fun convert(source: ContractEvent): MakeOffer {
        TODO("Not yet implemented")
    }
}