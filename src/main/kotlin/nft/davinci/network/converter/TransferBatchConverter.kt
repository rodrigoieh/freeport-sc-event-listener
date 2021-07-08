package nft.davinci.network.converter

import nft.davinci.event.TransferBatch
import nft.davinci.network.dto.DecodedContractEvent
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

    override fun convert(source: DecodedContractEvent): TransferBatch {
        return TransferBatch(
            source.getParamStringValue(PARAM_OPERATOR),
            source.getParamStringValue(PARAM_FROM),
            source.getParamStringValue(PARAM_TO),
            source.getParamArrayValues(PARAM_IDS),
            source.getParamArrayValues(PARAM_AMOUNTS).map(String::toBigInteger)
        )
    }
}
