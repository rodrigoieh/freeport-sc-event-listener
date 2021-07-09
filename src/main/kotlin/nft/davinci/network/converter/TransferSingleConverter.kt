package nft.davinci.network.converter

import nft.davinci.event.TransferSingle
import nft.davinci.network.dto.ContractEvent
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

    override fun convert(source: ContractEvent): TransferSingle {
        val (blockSignedAt, _, txHash, decoded) = source
        requireNotNull(decoded)
        return TransferSingle(
            blockSignedAt,
            txHash,
            decoded.getParamStringValue(PARAM_OPERATOR),
            decoded.getParamStringValue(PARAM_FROM),
            decoded.getParamStringValue(PARAM_TO),
            decoded.getParamStringValue(PARAM_ID),
            decoded.getParamStringValue(PARAM_AMOUNT).toBigInteger()
        )
    }
}
