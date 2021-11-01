package nft.freeport.auction

import nft.freeport.event.SettleAuction
import nft.freeport.network.config.ContractsConfig
import nft.freeport.network.converter.AbiDecoder
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SettleAuctionConverter(
    private val contractsConfig: ContractsConfig,
    private val abiDecoder: AbiDecoder
) : ContractEventConverter<SettleAuction> {
    override fun canConvert(source: ContractEvent): Boolean {
        return source.rawLogTopics.firstOrNull() == eventTopic(contractsConfig, SettleAuction::class.java)
    }

    override fun convert(source: ContractEvent): SettleAuction {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return SettleAuction(
            source.blockSignedAt,
            source.txHash,
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeUint256(output[0]),
            abiDecoder.decodeAddress(output[1])
        )
    }
}