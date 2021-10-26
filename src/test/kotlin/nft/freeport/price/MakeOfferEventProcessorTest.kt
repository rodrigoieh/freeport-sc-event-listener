package nft.freeport.price

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.MakeOffer
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
    fun `Process make offer event`() {
        //given
        val event = MakeOffer(
            "2021-07-08T00:47:30Z",
            "0xcafebabe",
            "0x123",
            "0xabc",
            BigInteger.TEN
        )

        //when
        testSubject.process(event)

        //then
        assertThat(MakeOfferEntity.findById(MakeOfferEntityId("0x123",  "0xabc")), notNullValue())
    }
}