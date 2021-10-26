package nft.freeport.price

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.TakeOffer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class TakeOfferEventProcessorTest {
    @Inject
    internal lateinit var testSubject: TakeOfferEventProcessor

    @Test
    fun `Supports TakeOffer event`() {
        assertThat(testSubject.supportedClass, equalTo(TakeOffer::class.java))
    }

    @Test
    fun `Process take offer event`() {
        //given
        val event = TakeOffer(
            "2021-07-08T00:47:30Z",
            "0xcafebabe",
            "0x123",
            "0xabc",
            "123",
            BigInteger.TEN,
            BigInteger.TWO
        )

        //when
        testSubject.process(event)

        //then
        assertThat(TakeOfferEntity.count(), equalTo(1L))
    }
}