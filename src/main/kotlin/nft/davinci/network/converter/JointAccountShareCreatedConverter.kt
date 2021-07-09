package nft.davinci.network.converter

import nft.davinci.event.JointAccountShareCreated
import nft.davinci.network.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JointAccountShareCreatedConverter : DecodedContractEventConverter<JointAccountShareCreated> {
    private companion object {
        private const val PARAM_ACCOUNT = "_account"
        private const val PARAM_OWNER = "_owner"
        private const val PARAM_FRACTION = "_fraction"
    }

    override val supportedClass = JointAccountShareCreated::class.java

    override fun convert(source: ContractEvent): JointAccountShareCreated {
        val (blockSignedAt, _, txHash, decoded) = source
        requireNotNull(decoded)
        return JointAccountShareCreated(
            blockSignedAt,
            txHash,
            decoded.getParamStringValue(PARAM_ACCOUNT),
            decoded.getParamStringValue(PARAM_OWNER),
            decoded.getParamStringValue(PARAM_FRACTION).toInt()
        )
    }
}
