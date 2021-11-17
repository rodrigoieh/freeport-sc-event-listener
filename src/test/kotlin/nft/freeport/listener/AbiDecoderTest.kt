package nft.freeport.listener

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class AbiDecoderTest {
    private val testSubject = AbiDecoder()

    @Test
    fun `Decode address`() {
        //given
        val input = "0x000000000000000000000000db9875e9a78b8e14bdb602b7b4c12dac4ea7c77c"

        //when
        val result = testSubject.decodeAddress(input)

        //then
        assertThat(result, equalTo("0xdb9875e9a78b8e14bdb602b7b4c12dac4ea7c77c"))
    }

    @Test
    fun `Decode uint256`() {
        //given
        val input = "0x0000000000000000000000000000000000000000000000000000000000002328"

        //when
        val result = testSubject.decodeUint256(input)

        //then
        assertThat(result, equalTo(9000.toBigInteger()))
    }

    @Test
    fun `Decode CID`() {
        //given
        val input = "0x017dfd85d4f6cb4dcd715a88101f7b1f06cd1e009b2327a0809d01eb9c91f231"

        //when
        val result = testSubject.decodeCid(input)

        //then
        assertThat(result, equalTo("QmNSUYVKDSvPUnRLKmuxk9diJ6yS96r1TrAXzjTiBcCLAL"))
    }
}