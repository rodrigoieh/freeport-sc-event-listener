package nft.freeport.nft

import nft.freeport.event.AttachToNFT
import nft.freeport.network.config.ContractsConfig
import nft.freeport.network.converter.AbiDecoder
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AttachToNFTConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<AttachToNFT> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, AttachToNFT::class.java)
    }

    override fun convert(source: ContractEvent): AttachToNFT {
        requireNotNull(source.rawLogData)
        return AttachToNFT(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeCid(source.rawLogData),
        )
    }
}