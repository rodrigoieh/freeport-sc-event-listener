package nft.freeport.nft

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.AttachToNFT
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class AttachToNFTEventProcessorTest {
    @Inject
    internal lateinit var testSubject: AttachToNFTEventProcessor

    @Test
    fun `Supports AttachToNFT event`() {
        assertThat(testSubject.supportedClass, equalTo(AttachToNFT::class.java))
    }

    @Test
    @TestTransaction
    fun `Process AttachToNFT event`() {
        //given
        val event = AttachToNFT(
            "2021-11-08T10:50:36Z",
            "0xf949e431a856e56d59f5b450c9bcf2a7dd1346fd54e6cdce5e24fb3fe88c5480",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            "QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"
        )

        //when
        testSubject.process(event)

        //then
        requireNotNull(NftCidEntity.findAll().firstResult()).apply {
            assertThat(sender, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
            assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
            assertThat(cid, equalTo("QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"))
        }
    }
}