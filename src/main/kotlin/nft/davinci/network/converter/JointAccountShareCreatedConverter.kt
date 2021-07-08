package nft.davinci.network.converter

import nft.davinci.event.JointAccountShareCreated
import nft.davinci.network.dto.DecodedContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JointAccountShareCreatedConverter : DecodedContractEventConverter<JointAccountShareCreated> {
    private companion object {
        private const val PARAM_ACCOUNT = "_account"
        private const val PARAM_OWNER = "_owner"
        private const val PARAM_FRACTION = "_fraction"
    }

    override val supportedClass = JointAccountShareCreated::class.java

    override fun convert(source: DecodedContractEvent): JointAccountShareCreated {
        return JointAccountShareCreated(
            source.getParamStringValue(PARAM_ACCOUNT),
            source.getParamStringValue(PARAM_OWNER),
            source.getParamStringValue(PARAM_FRACTION).toInt()
        )
    }
}
