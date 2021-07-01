package nft.davinci.network.dto

data class DecodedContractEvent(
    val name: String,
    val signature: String,
    val params: List<ContractEventParam>
) {
    private companion object {
        private const val NAME_TRANSFER_SINGLE = "TransferSingle"

        private const val PARAM_OPERATOR = "_operator"
        private const val PARAM_FROM = "_from"
        private const val PARAM_TO = "_to"
        private const val PARAM_ID = "_id"
        private const val PARAM_AMOUNT = "_amount"

        private const val ZERO_ADDRESS = "0x0000000000000000000000000000000000000000"
    }

    fun toNftEvent(): NftEvent? {
        if (name != NAME_TRANSFER_SINGLE) {
            return null
        }

        val operator = getParamValue(PARAM_OPERATOR)
        val from = getParamValue(PARAM_FROM)
        val to = getParamValue(PARAM_TO)
        val nftId = getParamValue(PARAM_ID)
        val quantity = getParamValue(PARAM_AMOUNT).toLong()

        return when {
            from == ZERO_ADDRESS -> NftMinted(operator, to, nftId, quantity)
            to == ZERO_ADDRESS -> NftBurned(operator, from, nftId, quantity)
            else -> NftTransferred(operator, from, to, nftId, quantity)
        }
    }

    private fun getParamValue(paramName: String): String {
        return params.first { it.name == paramName }.value
    }
}
