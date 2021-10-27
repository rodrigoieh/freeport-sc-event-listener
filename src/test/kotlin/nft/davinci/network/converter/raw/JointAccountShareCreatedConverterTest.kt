package nft.davinci.network.converter.raw

import nft.davinci.network.config.ContractConfig
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.dto.ContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

internal class JointAccountShareCreatedConverterTest {
    private val davinciConfig: ContractConfig = mock {
        on { eventTopics() } doReturn mapOf("JointAccountShareCreated" to "0x006fb9851f1fd2fbc9fa36680d17e1254999a38e5f3c76c3a1ecc126a464601b")
    }

    private val contractsConfig: ContractsConfig = mock {
        on { contracts() } doReturn mapOf("davinci" to davinciConfig)
    }

    private val testSubject = JointAccountShareCreatedConverter(contractsConfig, AbiDecoder())

    private val source = ContractEvent(
        "2021-07-09T10:51:42Z",
        16124507,
        "0xfe90c2a74aaf2dd3efd1bf52e5b77582a3801deac70f02bca5a7db372838db18",
        listOf(
            "0x006fb9851f1fd2fbc9fa36680d17e1254999a38e5f3c76c3a1ecc126a464601b",
            "0x000000000000000000000000db9875e9a78b8e14bdb602b7b4c12dac4ea7c77c",
            "0x0000000000000000000000006d2b28389d3153689c57909829dfcf6241d36388"
        ),
        "0x0000000000000000000000000000000000000000000000000000000000002328",
        null
    )

    @Test
    fun `Can convert JointAccountShareCreated`() {
        assertTrue(testSubject.canConvert(source))
    }

    @Test
    fun `Can't convert other events`() {
        assertFalse(testSubject.canConvert(source.copy(rawLogTopics = listOf())))
    }

    @Test
    fun `Convert event`() {
        //when
        val (blockSignedAt, txHash, account, owner, fraction) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-07-09T10:51:42Z"))
        assertThat(txHash, equalTo("0xfe90c2a74aaf2dd3efd1bf52e5b77582a3801deac70f02bca5a7db372838db18"))
        assertThat(account, equalTo("0xdb9875e9a78b8e14bdb602b7b4c12dac4ea7c77c"))
        assertThat(owner, equalTo("0x6d2b28389d3153689c57909829dfcf6241d36388"))
        assertThat(fraction, equalTo(9000))
    }
}
