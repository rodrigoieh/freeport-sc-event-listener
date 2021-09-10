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
import java.math.BigInteger

internal class TakeOfferConverterTest {
    private val davinciConfig: ContractConfig = mock {
        on { eventTopics() } doReturn mapOf("TakeOffer" to "0xe90359125940e7c9b26005d19d4d8a2a5335ea8bef094bcd61bb8f8091cad117")
    }

    private val contractsConfig: ContractsConfig = mock {
        on { contracts() } doReturn mapOf("davinci" to davinciConfig)
    }

    private val testSubject = TakeOfferConverter(contractsConfig, AbiDecoder())

    private val source = ContractEvent(
        "2021-09-08T16:14:59Z",
        18673605,
        "0xe36d128601568807229048eb55c6d64bc278233e14d38e23c3c5e7673e475260",
        listOf(
            "0xe90359125940e7c9b26005d19d4d8a2a5335ea8bef094bcd61bb8f8091cad117",
            "0x00000000000000000000000063846e2d234e4f854f43423014430b4e131f697b",
            "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
            "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
        ),
        "0x00000000000000000000000000000000000000000000000000000574fbde60000000000000000000000000000000000000000000000000000000000000000001",
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
        val (blockSignedAt, txHash, buyer, seller, nftId, price, amount) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-09-08T16:14:59Z"))
        assertThat(
            txHash,
            equalTo("0xe36d128601568807229048eb55c6d64bc278233e14d38e23c3c5e7673e475260")
        )
        assertThat(buyer, equalTo("0x63846e2d234e4f854f43423014430b4e131f697b"))
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(
            nftId,
            equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802")
        )
        assertThat(price, equalTo(6000000000000.toBigInteger()))
        assertThat(amount, equalTo(BigInteger.ONE))
    }
}