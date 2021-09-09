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

internal class SetExchangeRateConverterTest {
    private val contractConfig: ContractConfig = mock {
        on { eventTopics() } doReturn mapOf("SetExchangeRate" to "0x1d5de90e7c5b244ac5797698b15fe80a92524d933dafd79e001daf844555fb1c")
    }

    private val contractsConfig: ContractsConfig = mock {
        on { contracts() } doReturn mapOf("fiat-to-nft" to contractConfig)
    }

    private val testSubject = SetExchangeRateConverter(contractsConfig, AbiDecoder())

    private val source = ContractEvent(
        "2021-09-08T08:37:55Z",
        18673605,
        "0x73300b05794c305a12696a17db2de8b4dd3a78ff913ab4634657044a80803aa9",
        listOf(
            "0x1d5de90e7c5b244ac5797698b15fe80a92524d933dafd79e001daf844555fb1c",
        ),
        "0x000000000000000000000000000000000000000000000000000000003b9aca00",
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
        val (blockSignedAt, txHash, cereUnitsPerPenny) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-09-08T08:37:55Z"))
        assertThat(
            txHash,
            equalTo("0x73300b05794c305a12696a17db2de8b4dd3a78ff913ab4634657044a80803aa9")
        )
        assertThat(cereUnitsPerPenny, equalTo(1000000000.toBigInteger()))
    }
}