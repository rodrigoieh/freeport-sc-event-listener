package nft.davinci.network.converter.raw

import nft.davinci.event.JointAccountShareCreated
import nft.davinci.network.NetworkConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JointAccountShareCreatedConverter(
    networkConfig: NetworkConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<JointAccountShareCreated> {
    private val topic = networkConfig.eventTopics().getValue(JointAccountShareCreated::class.java.simpleName)

    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == topic
    }

    override fun convert(source: ContractEvent): JointAccountShareCreated {
        return JointAccountShareCreated(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeAddress(source.rawLogTopics[2]),
            abiDecoder.decodeUint256(source.rawLogData).toInt()
        )
    }
}
