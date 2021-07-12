package nft.davinci.network.converter.standard

import nft.davinci.event.TransferSingle
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferSingleConverter : ContractEventConverter<TransferSingle> {
    private companion object {
        private const val PARAM_OPERATOR = "_operator"
        private const val PARAM_FROM = "_from"
        private const val PARAM_TO = "_to"
        private const val PARAM_ID = "_id"
        private const val PARAM_AMOUNT = "_amount"
    }

    override fun canConvert(source: ContractEvent): Boolean {
        return source.decoded?.name == TransferSingle::class.java.simpleName
    }

    override fun convert(source: ContractEvent): TransferSingle {
        requireNotNull(source.decoded)
        return TransferSingle(
            source.blockSignedAt,
            source.txHash,
            source.decoded.getParamStringValue(PARAM_OPERATOR),
            source.decoded.getParamStringValue(PARAM_FROM),
            source.decoded.getParamStringValue(PARAM_TO),
            source.decoded.getParamStringValue(PARAM_ID),
            source.decoded.getParamStringValue(PARAM_AMOUNT).toBigInteger()
        )
    }
}
