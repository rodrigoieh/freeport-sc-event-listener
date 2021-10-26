package nft.freeport.ja

import nft.freeport.event.JointAccountShareCreated
import nft.freeport.network.config.ContractsConfig
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.converter.AbiDecoder
import nft.freeport.network.dto.ContractEvent
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
