package nft.davinci.network.converter.raw

import nft.davinci.event.RoyaltiesConfigured
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RoyaltiesConfiguredConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<RoyaltiesConfigured> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, RoyaltiesConfigured::class.java)
    }

    override fun convert(source: ContractEvent): RoyaltiesConfigured {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return RoyaltiesConfigured(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeUint256(source.rawLogTopics[1]).toString(),
            abiDecoder.decodeAddress(output[0]),
            abiDecoder.decodeUint256(output[1]).toInt(),
            abiDecoder.decodeUint256(output[2]),
            abiDecoder.decodeAddress(output[3]),
            abiDecoder.decodeUint256(output[4]).toInt(),
            abiDecoder.decodeUint256(output[5])
        )
    }
}
