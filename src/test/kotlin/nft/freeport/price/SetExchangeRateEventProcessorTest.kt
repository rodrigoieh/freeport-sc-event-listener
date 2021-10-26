package nft.freeport.price

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.SetExchangeRate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class SetExchangeRateEventProcessorTest {
    @Inject
    internal lateinit var testSubject: SetExchangeRateEventProcessor

    @Test
    fun `Supports SetExchangeRate event`() {
        assertThat(testSubject.supportedClass, equalTo(SetExchangeRate::class.java))
    }

    @Test
    fun `Process SetExchangeRate event`() {
        //given
        val event = SetExchangeRate(
            "2021-07-08T00:47:30Z",
            "0xcafebabe",
            BigInteger.TEN,
        )

        //when
        testSubject.process(event)

        //then
        assertThat(ExchangeRateEntity.findById(BigInteger.TEN), notNullValue())
    }
}