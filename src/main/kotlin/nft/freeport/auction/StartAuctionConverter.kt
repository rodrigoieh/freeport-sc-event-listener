package nft.freeport.auction

import nft.freeport.event.StartAuction
import nft.freeport.network.config.ContractsConfig
import nft.freeport.network.converter.AbiDecoder
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StartAuctionConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<StartAuction> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, StartAuction::class.java)
    }

    override fun convert(source: ContractEvent): StartAuction {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return StartAuction(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeUint256(output[0]),
            abiDecoder.decodeUint256(output[1]),
        )
    }

}