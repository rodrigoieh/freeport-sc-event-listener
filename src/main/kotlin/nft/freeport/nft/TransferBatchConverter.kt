package nft.freeport.nft

import nft.freeport.event.TransferBatch
import nft.freeport.network.converter.ContractEventConverter
import nft.freeport.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferBatchConverter : ContractEventConverter<TransferBatch> {
    private companion object {
        private const val PARAM_OPERATOR = "_operator"
        private const val PARAM_FROM = "_from"
        private const val PARAM_TO = "_to"
        private const val PARAM_IDS = "_ids"
        private const val PARAM_AMOUNTS = "_amounts"
    }

    override fun canConvert(source: ContractEvent): Boolean {
        return source.decoded?.name == TransferBatch::class.java.simpleName
    }

    override fun convert(source: ContractEvent): TransferBatch {
        requireNotNull(source.decoded)
        return TransferBatch(
            source.blockSignedAt,
            source.txHash,
            source.decoded.getParamStringValue(PARAM_OPERATOR),
            source.decoded.getParamStringValue(PARAM_FROM),
            source.decoded.getParamStringValue(PARAM_TO),
            source.decoded.getParamArrayValues(PARAM_IDS),
            source.decoded.getParamArrayValues(PARAM_AMOUNTS).map(String::toBigInteger)
        )
    }
}
