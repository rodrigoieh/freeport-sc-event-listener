package nft.davinci.network.converter

import nft.davinci.event.TransferSingle
import nft.davinci.network.dto.DecodedContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TransferSingleConverter : DecodedContractEventConverter<TransferSingle> {
    private companion object {
        private const val PARAM_OPERATOR = "_operator"
        private const val PARAM_FROM = "_from"
        private const val PARAM_TO = "_to"
        private const val PARAM_ID = "_id"
        private const val PARAM_AMOUNT = "_amount"
    }

    override val supportedClass = TransferSingle::class.java

    override fun convert(source: DecodedContractEvent): TransferSingle {
        return TransferSingle(
            source.getParamStringValue(PARAM_OPERATOR),
            source.getParamStringValue(PARAM_FROM),
            source.getParamStringValue(PARAM_TO),
            source.getParamStringValue(PARAM_ID),
            source.getParamStringValue(PARAM_AMOUNT).toBigInteger()
        )
    }
}
