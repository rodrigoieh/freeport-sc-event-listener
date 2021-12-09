package nft.freeport.processor.freeport.price

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.listener.event.MakeOffer
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.contractEvent
import nft.freeport.processor.freeport.nft.NftEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class MakeOfferEventProcessorTest {
    @Inject
    internal lateinit var testSubject: MakeOfferEventProcessor

    @Test
    fun `Supports MakeOffer event`() {
        assertThat(testSubject.supportedClass, equalTo(MakeOffer::class.java))
    }

    @Test
    @TestTransaction
    fun `Process make offer event`() {
        //given
        NftEntity(
            "0xabc",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            BigInteger.ONE
        ).persist()
        val event = MakeOffer(
            "0x123",
            "0xabc",
            BigInteger.TEN
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        assertThat(MakeOfferEntity.findById(MakeOfferEntityId("0x123", "0xabc")), notNullValue())
    }
}