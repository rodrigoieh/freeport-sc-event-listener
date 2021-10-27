package nft.davinci.network.processor.price

import kotlinx.coroutines.runBlocking
import nft.davinci.event.TakeOffer
import nft.davinci.price.PriceRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import java.math.BigInteger

internal class TakeOfferEventProcessorTest {
    private val priceRepository: PriceRepository = mock()

    private val testSubject = TakeOfferEventProcessor(priceRepository)

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
        runBlocking { testSubject.process(event) }

        //then
        verifyBlocking(priceRepository) { createTakeOffer(event) }
    }
}