package nft.freeport.auction

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.network.dto.ContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class SettleAuctionConverterTest {
    @Inject
    internal lateinit var testSubject: SettleAuctionConverter

    private val source = ContractEvent(
        "2021-10-28T12:28:52Z",
        20745586,
        "0x48b22b9351de03360edaa6de9fc1255f85dc8c669875bbf8e30eb6c357ea5483",
        listOf(
            "0xfe2c1531a975fce0584787c5e2643df8c1fe92f870c9dbadb24e366e31e79f44",
            "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
            "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
        ),
        "0x0000000000000000000000000000000000000000000000000000002e90edd00000000000000000000000000063846e2d234e4f854f43423014430b4e131f697b",
        null
    )

    @Test
    fun `Can convert SettleAuction`() {
        assertTrue(testSubject.canConvert(source))
    }

    @Test
    fun `Can't convert other events`() {
        assertFalse(testSubject.canConvert(source.copy(rawLogTopics = listOf())))
    }

    @Test
    fun `Convert event`() {
        //when
        val (blockSignedAt, txHash, seller, nftId, price, buyer) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-10-28T12:28:52Z"))
        assertThat(
            txHash,
            equalTo("0x48b22b9351de03360edaa6de9fc1255f85dc8c669875bbf8e30eb6c357ea5483")
        )
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(
            nftId,
            equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802")
        )
        assertThat(price, equalTo(200000000000.toBigInteger()))
        assertThat(buyer, equalTo("0x63846e2d234e4f854f43423014430b4e131f697b"))
    }
}