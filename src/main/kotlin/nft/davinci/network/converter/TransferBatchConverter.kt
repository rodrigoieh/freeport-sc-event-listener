package nft.davinci.network.converter

import nft.davinci.event.TransferBatch
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferBatchConverter : DecodedContractEventConverter<TransferBatch> {
    private companion object {
        private const val PARAM_OPERATOR = "_operator"
        private const val PARAM_FROM = "_from"
        private const val PARAM_TO = "_to"
        private const val PARAM_IDS = "_ids"
        private const val PARAM_AMOUNTS = "_amounts"
    }

    override val supportedClass = TransferBatch::class.java

    override fun convert(source: ContractEvent): TransferBatch {
        val (blockSignedAt, _, txHash, decoded) = source
        requireNotNull(decoded)
        return TransferBatch(
            blockSignedAt,
            txHash,
            decoded.getParamStringValue(PARAM_OPERATOR),
            decoded.getParamStringValue(PARAM_FROM),
            decoded.getParamStringValue(PARAM_TO),
            decoded.getParamArrayValues(PARAM_IDS),
            decoded.getParamArrayValues(PARAM_AMOUNTS).map(String::toBigInteger)
        )
    }
}
