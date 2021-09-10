package nft.davinci.network.converter.raw

import nft.davinci.event.TakeOffer
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TakeOfferConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<TakeOffer> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, TakeOffer::class.java)
    }

    override fun convert(source: ContractEvent): TakeOffer {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return TakeOffer(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeAddress(source.rawLogTopics[2]),
            abiDecoder.decodeUint256(source.rawLogTopics[3]).toString(),
            abiDecoder.decodeUint256(output[0]),
            abiDecoder.decodeUint256(output[1])
        )
    }
}