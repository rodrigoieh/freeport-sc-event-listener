package nft.davinci.network.converter.raw

import nft.davinci.event.MakeOffer
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MakeOfferConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<MakeOffer> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, MakeOffer::class.java)
    }

    override fun convert(source: ContractEvent): MakeOffer {
        TODO("Not yet implemented")
    }
}