package nft.freeport.processor.freeport.royalty

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.listener.event.RoyaltiesConfigured
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.contractEvent
import nft.freeport.processor.freeport.nft.NftEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class RoyaltiesConfiguredEventProcessorTest {
    @Inject
    internal lateinit var testSubject: RoyaltiesConfiguredEventProcessor

    @Test
    fun `Supports RoyaltiesConfigured event`() {
        assertThat(testSubject.supportedClass, equalTo(RoyaltiesConfigured::class.java))
    }

    @Test
    @TestTransaction
    fun `Process royalties configured event`() {
        //given
        NftEntity(
            "123",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            BigInteger.ONE
        ).persist()
        val event = RoyaltiesConfigured(
            "123",
            "0x123",
            5,
            BigInteger.TWO,
            "0xabc",
            6,
            BigInteger.TEN
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        assertThat(RoyaltyEntity.count(), equalTo(2L))
    }
}
