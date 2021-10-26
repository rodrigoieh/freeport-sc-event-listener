package nft.freeport.nft

import nft.freeport.event.NftEvent
import nft.freeport.event.NftMinted
import nft.freeport.event.NftTransferred
import nft.freeport.event.TransferSingle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import java.math.BigInteger

internal class TransferSingleEventProcessorTest {
    private val nftEventProcessor: NftEventProcessor = mock()

    private val testSubject = TransferSingleEventProcessor(nftEventProcessor)

    @Test
    fun `Supports TransferSingle event`() {
        assertThat(testSubject.supportedClass, equalTo(TransferSingle::class.java))
    }

    @Test
    fun `Process NFT minted event`() {
        //given
        val event = TransferSingle(
            "2021-07-08T00:47:30Z",
            "0xcafebabe",
            "0x123",
            "0x0000000000000000000000000000000000000000",
            "0xdef",
            "123",
            BigInteger.TEN
        )

        //when
        testSubject.process(event)

        //then
        argumentCaptor<NftEvent>().apply {
            verifyBlocking(nftEventProcessor) {
                onNftEvent(
                    capture(),
                    eq("2021-07-08T00:47:30Z"),
                    eq("0xcafebabe")
                )
            }
            assertThat(firstValue, instanceOf(NftMinted::class.java))
        }
    }

    @Test
    fun `Process NFT transferred event`() {
        //given
        val event = TransferSingle(
            "2021-07-08T00:47:30Z",
            "0xcafebabe",
            "0x123",
            "0xabc",
            "0xdef",
            "123",
            BigInteger.TEN
        )

        //when
        testSubject.process(event)

        //then
        argumentCaptor<NftEvent>().apply {
            verifyBlocking(nftEventProcessor) {
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
