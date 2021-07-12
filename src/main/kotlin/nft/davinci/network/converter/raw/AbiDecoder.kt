package nft.davinci.network.converter.raw

import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AbiDecoder {
    private companion object {
        private const val HEX_PREFIX = "0x"
        private const val ADDRESS_LENGTH = 40
    }

    fun decodeAddress(input: String) = "$HEX_PREFIX${input.takeLast(ADDRESS_LENGTH)}"

    fun decodeUint256(input: String): BigInteger {
        return input.removePrefix(HEX_PREFIX).toBigInteger(16)
    }
}
