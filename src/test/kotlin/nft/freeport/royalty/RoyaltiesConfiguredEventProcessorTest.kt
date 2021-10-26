package nft.freeport.royalty

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.RoyaltiesConfigured
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
    fun `Process royalties configured event`() {
        //given
        val event = RoyaltiesConfigured(
            "2021-07-08T00:47:30Z",
            "0xcafebabe",
            "123",
            "0x123",
            5,
            BigInteger.TWO,
            "0xabc",
            6,
            BigInteger.TEN
        )

        //when
        testSubject.process(event)

        //then
        assertThat(RoyaltyEntity.count(), equalTo(2L))
    }
}
