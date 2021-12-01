package nft.freeport.processor.freeport.nft

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
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            BigInteger.ONE
        ).persist()
        val event = AttachToNFT(
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            "QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"
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
}