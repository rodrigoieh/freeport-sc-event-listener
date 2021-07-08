package nft.davinci.network.processor.nft

import kotlinx.coroutines.runBlocking
import nft.davinci.event.NftEvent
import nft.davinci.event.NftTransferred
import nft.davinci.event.TransferBatch
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking
import java.math.BigInteger

internal class TransferBatchEventProcessorTest {
    private val nftEventProcessor: NftEventProcessor = mock()

    private val testSubject = TransferBatchEventProcessor(nftEventProcessor)

    @Test
    fun `Supports TransferBatch event`() {
        assertThat(testSubject.supportedClass, equalTo(TransferBatch::class.java))
    }

    @Test
    fun `Process NFT transferred event`() {
        //given
        val event =
            TransferBatch("0x123", "0xabc", "0xdef", listOf("123", "456"), listOf(BigInteger.ONE, BigInteger.TEN))

        //when
        runBlocking { testSubject.process(event) }

        //then
        argumentCaptor<NftEvent>().apply {
            verifyBlocking(nftEventProcessor, times(2)) { onNftEvent(capture()) }
            assertThat(firstValue, instanceOf(NftTransferred::class.java))
        }
    }
}
