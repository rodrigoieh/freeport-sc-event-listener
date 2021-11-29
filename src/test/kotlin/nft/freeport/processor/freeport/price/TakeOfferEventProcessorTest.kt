package nft.freeport.processor.freeport.price

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.AbstractIntegrationTest
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TakeOffer
import nft.freeport.processor.freeport.contractEvent
import nft.freeport.processor.freeport.nft.NftEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class TakeOfferEventProcessorTest : AbstractIntegrationTest() {
    @Inject
    internal lateinit var testSubject: TakeOfferEventProcessor

    @Test
    fun `Supports TakeOffer event`() {
        assertThat(testSubject.supportedClass, equalTo(TakeOffer::class.java))
    }

    @Test
    @TestTransaction
    fun `Process take offer event`() {
        //given
        NftEntity(
            "123",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            BigInteger.ONE
        ).persist()
        val event = TakeOffer(
            "0x123",
            "0xabc",
            "123",
            BigInteger.TEN,
            BigInteger.TWO
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        assertThat(TakeOfferEntity.count(), equalTo(1L))
    }
}