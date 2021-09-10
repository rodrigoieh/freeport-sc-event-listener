package nft.davinci.network.processor.price

import kotlinx.coroutines.runBlocking
import nft.davinci.event.SetExchangeRate
import nft.davinci.price.PriceRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import java.math.BigInteger

internal class SetExchangeRateProcessorTest {
    private val priceRepository: PriceRepository = mock()

    private val testSubject = SetExchangeRateProcessor(priceRepository)

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
        runBlocking { testSubject.process(event) }

        //then
        verifyBlocking(priceRepository) { updateExchangeRate(BigInteger.TEN) }
    }
}