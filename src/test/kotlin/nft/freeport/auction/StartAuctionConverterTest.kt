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
internal class StartAuctionConverterTest {
    @Inject
    internal lateinit var testSubject: StartAuctionConverter

    private val source = ContractEvent(
        "2021-10-28T11:58:06Z",
        20744691,
        "0xcb5e3b549f6db27b056fe5832b4777ba9f48fd8cb16ff743899c4981fc390806",
        listOf(
            "0x5135842dc9522996ca3d92189d0ded7e70ecbfc5545c115def0c7bdb9ee41f2b",
            "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
            "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
        ),
        "0x000000000000000000000000000000000000000000000000000000152a9aa45d00000000000000000000000000000000000000000000000000000000617a90c3",
        null
    )

    @Test
    fun `Can convert StartAuction`() {
        assertTrue(testSubject.canConvert(source))
    }

    @Test
    fun `Can't convert other events`() {
        assertFalse(testSubject.canConvert(source.copy(rawLogTopics = listOf())))
    }

    @Test
    fun `Convert event`() {
        //when
        val (blockSignedAt, txHash, seller, nftId, price, closeTimeSec) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-10-28T11:58:06Z"))
        assertThat(
            txHash,
            equalTo("0xcb5e3b549f6db27b056fe5832b4777ba9f48fd8cb16ff743899c4981fc390806")
        )
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
        assertThat(price, equalTo(90909090909.toBigInteger()))
        assertThat(closeTimeSec, equalTo(1635422403.toBigInteger()))
    }
}