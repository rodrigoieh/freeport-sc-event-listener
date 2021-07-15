package nft.davinci.network.processor.royalty

import kotlinx.coroutines.runBlocking
import nft.davinci.event.RoyaltiesConfigured
import nft.davinci.royalty.RoyaltyRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import java.math.BigInteger

internal class RoyaltiesConfiguredEventProcessorTest {
    private val royaltyRepository: RoyaltyRepository = mock()

    private val testSubject = RoyaltiesConfiguredEventProcessor(royaltyRepository)

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
        runBlocking { testSubject.process(event) }

        //then
        verifyBlocking(royaltyRepository) { save(event) }
    }
}
