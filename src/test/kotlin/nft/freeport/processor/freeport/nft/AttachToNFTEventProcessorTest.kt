package nft.freeport.processor.freeport.nft

import io.kotest.matchers.shouldBe
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.AbstractIntegrationTest
import nft.freeport.listener.event.AttachToNFT
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.contractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class AttachToNFTEventProcessorTest : AbstractIntegrationTest() {
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
        NftEntity(
            nftId = "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            minter = "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            supply = BigInteger.ONE
        ).persist()

        val event = AttachToNFT(
            sender = "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            nftId = "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            cid = "QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-11-08T10:50:36Z")))

        //then
        requireNotNull(NftCidEntity.findAll().firstResult()).apply {
            assertThat(sender, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
            assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
            assertThat(cid, equalTo("QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"))
        }
    }

    @Test
    @TestTransaction
    fun `AttachToNFT event from non-minter (sender != minter) -- event isn't processed`() {
        //given
        val nftId = "123456789"
        NftEntity(nftId = nftId, minter = "0x1", supply = BigInteger.ONE).persist()

        val event = AttachToNFT(
            sender = "0x2", nftId = nftId,
            cid = "QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-11-08T10:50:36Z")))

        //then
        NftCidEntity.findAll().firstResult() shouldBe null
    }
}