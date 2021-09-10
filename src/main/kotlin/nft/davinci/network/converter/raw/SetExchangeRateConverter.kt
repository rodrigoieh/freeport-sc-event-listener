package nft.davinci.network.converter.raw

import nft.davinci.event.SetExchangeRate
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SetExchangeRateConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<SetExchangeRate> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, SetExchangeRate::class.java)
    }

    override fun convert(source: ContractEvent): SetExchangeRate {
        requireNotNull(source.rawLogData)
        return SetExchangeRate(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeUint256(source.rawLogData)
        )
    }
}