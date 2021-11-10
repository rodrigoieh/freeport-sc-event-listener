package nft.freeport.network.converter

import org.apache.commons.codec.binary.Hex
import org.komputing.kbase58.encodeToBase58String
import java.math.BigInteger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AbiDecoder {
    private companion object {
        private const val HEX_PREFIX = "0x"
        private const val ADDRESS_LENGTH = 40
        private const val CID_PREFIX = "1220" // function:0x12=sha2, size:0x20=256 bits
    }

    fun decodeAddress(input: String) = "$HEX_PREFIX${input.takeLast(ADDRESS_LENGTH)}"

    fun decodeUint256(input: String): BigInteger {
        return input.removePrefix(HEX_PREFIX).toBigInteger(16)
    }

    /**
     * Return base58 encoded ipfs hash from bytes32 hex string,
     * E.g. "0x017dfd85d4f6cb4dcd715a88101f7b1f06cd1e009b2327a0809d01eb9c91f231"
     * --> "QmNSUYVKDSvPUnRLKmuxk9diJ6yS96r1TrAXzjTiBcCLAL"
     */
    fun decodeCid(input: String) : String {
        val hashHex = CID_PREFIX + input.removePrefix(HEX_PREFIX)
        return Hex.decodeHex(hashHex).encodeToBase58String()
    }
}
