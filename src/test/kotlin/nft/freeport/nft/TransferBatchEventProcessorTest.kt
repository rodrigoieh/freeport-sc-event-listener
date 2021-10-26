package nft.freeport.nft

import kotlinx.coroutines.runBlocking
import nft.freeport.event.NftEvent
import nft.freeport.event.NftTransferred
import nft.freeport.event.TransferBatch
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
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
            TransferBatch(
                "2021-07-08T00:47:30Z",
                "0xcafebabe",
                "0x123",
                "0xabc",
                "0xdef",
                listOf("123", "456"),
                listOf(BigInteger.ONE, BigInteger.TEN)
            )

        //when
        runBlocking { testSubject.process(event) }

        //then
        argumentCaptor<NftEvent>().apply {
            verifyBlocking(nftEventProcessor, times(2)) {
                onNftEvent(
                    capture(),
                    eq("2021-07-08T00:47:30Z"),
                    eq("0xcafebabe")
                )
            }
            assertThat(firstValue, instanceOf(NftTransferred::class.java))
        }
    }
}
