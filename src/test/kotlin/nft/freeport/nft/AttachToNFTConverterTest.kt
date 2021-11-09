package nft.freeport.nft

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.network.dto.ContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class AttachToNFTConverterTest {
    @Inject
    internal lateinit var testSubject: AttachToNFTConverter

    private val source = ContractEvent(
        "2021-11-08T10:50:36Z",
        21202410,
        "0xf949e431a856e56d59f5b450c9bcf2a7dd1346fd54e6cdce5e24fb3fe88c5480",
        listOf(
            "0xcb0dbc631188ff7e4c5831ec907b2d9ca2786dd0314af3e43a7269821a19e2b4",
            "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
            "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
        ),
        "0x1122334455667788990011223344556677889900112233445566778899001122",
        null
    )

    @Test
    fun `Can convert AttachToNFT`() {
        assertTrue(testSubject.canConvert(source))
    }

    @Test
    fun `Can't convert other events`() {
        assertFalse(testSubject.canConvert(source.copy(rawLogTopics = listOf())))
    }

    @Test
    fun `Convert event`() {
        //when
        val (blockSignedAt, txHash, sender, nftId, cid) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-11-08T10:50:36Z"))
        assertThat(
            txHash,
            equalTo("0xf949e431a856e56d59f5b450c9bcf2a7dd1346fd54e6cdce5e24fb3fe88c5480")
        )
        assertThat(sender, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
        assertThat(cid, equalTo("QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"))
    }
}