package nft.davinci.network.processor.price

import kotlinx.coroutines.runBlocking
import nft.davinci.event.MakeOffer
import nft.davinci.price.PriceRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import java.math.BigInteger

internal class MakeOfferEventProcessorTest {
    private val priceRepository: PriceRepository = mock()

    private val testSubject = MakeOfferEventProcessor(priceRepository)

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
        runBlocking { testSubject.process(event) }

        //then
        verifyBlocking(priceRepository) { createOrUpdateMakeOffer(event) }
    }
}