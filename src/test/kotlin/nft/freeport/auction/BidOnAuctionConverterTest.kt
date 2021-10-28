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
internal class BidOnAuctionConverterTest {
    @Inject
    internal lateinit var testSubject: BidOnAuctionConverter

    private val source = ContractEvent(
        "2021-10-28T11:58:10Z",
        20744693,
        "0x4edd9e97e70f5410c09570d75807136318e34b23edc9e5efdea275a96b436458",
        listOf(
            "0x39e9b26db60de3ca88f045fdd8954028f1bbd0c6e2ff124121cb5a03da370191",
            "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
            "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
        ),
        "0x0000000000000000000000000000000000000000000000000000002e90edd00000000000000000000000000000000000000000000000000000000000617a931b00000000000000000000000063846e2d234e4f854f43423014430b4e131f697b",
        null
    )

    @Test
    fun `Can convert BidOnAuction`() {
        assertTrue(testSubject.canConvert(source))
    }

    @Test
    fun `Can't convert other events`() {
        assertFalse(testSubject.canConvert(source.copy(rawLogTopics = listOf())))
    }

    @Test
    fun `Convert event`() {
        //when
        val (blockSignedAt, txHash, seller, nftId, price, closeTimeSec, buyer) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-10-28T11:58:10Z"))
        assertThat(
            txHash,
            equalTo("0x4edd9e97e70f5410c09570d75807136318e34b23edc9e5efdea275a96b436458")
        )
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(
            nftId,
            equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802")
        )
        assertThat(price, equalTo(200000000000.toBigInteger()))
        assertThat(closeTimeSec, equalTo(1635423003.toBigInteger()))
        assertThat(buyer, equalTo("0x63846e2d234e4f854f43423014430b4e131f697b"))
    }
}