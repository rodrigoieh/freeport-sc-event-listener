package nft.davinci.network.converter.raw

import nft.davinci.event.MakeOffer
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
        requireNotNull(source.rawLogData)
        return MakeOffer(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeUint256(source.rawLogData)
        )
    }
}