package nft.freeport.processor.freeport.price

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.AbstractIntegrationTest
import nft.freeport.listener.event.SetExchangeRate
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.contractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class SetExchangeRateEventProcessorTest : AbstractIntegrationTest() {
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
            BigInteger.TEN,
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        assertThat(ExchangeRateEntity.findById(BigInteger.TEN), notNullValue())
    }
}