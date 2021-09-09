package nft.davinci.network.converter.raw

import nft.davinci.event.JointAccountShareCreated
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JointAccountShareCreatedConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<JointAccountShareCreated> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, JointAccountShareCreated::class.java)
    }

    override fun convert(source: ContractEvent): JointAccountShareCreated {
        requireNotNull(source.rawLogData)
        return JointAccountShareCreated(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeAddress(source.rawLogTopics[2]),
            abiDecoder.decodeUint256(source.rawLogData).toInt()
        )
    }
}
