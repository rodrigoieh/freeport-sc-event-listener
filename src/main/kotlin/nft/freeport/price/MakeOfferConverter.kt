package nft.freeport.price

import nft.freeport.event.MakeOffer
import nft.freeport.network.config.ContractsConfig
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.converter.AbiDecoder
import nft.freeport.network.dto.ContractEvent
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