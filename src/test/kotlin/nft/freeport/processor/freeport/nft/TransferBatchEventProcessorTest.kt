package nft.freeport.processor.freeport.nft

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.AbstractIntegrationTest
import nft.freeport.listener.event.TransferBatch
import nft.freeport.processor.freeport.eventEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class TransferBatchEventProcessorTest : AbstractIntegrationTest() {
    @Inject
    internal lateinit var testSubject: TransferBatchEventProcessor

    @Test
    fun `Supports TransferBatch event`() {
        assertThat(testSubject.supportedClass, equalTo(TransferBatch::class.java))
    }

    @Test
    @TestTransaction
    fun `Process event`() {
        //given
        WalletNftEntity(WalletNftEntityId("123", "0xabc"), BigInteger.TEN).persist()
        WalletNftEntity(WalletNftEntityId("456", "0xabc"), BigInteger.TEN).persist()
        val event =
            TransferBatch(
                "0x123",
                "0xabc",
                "0xdef",
                listOf("123", "456"),
                listOf(BigInteger.ONE, BigInteger.TEN)
            )

        //when
        testSubject.process(event, eventEntity("2021-07-08T00:47:30Z"))

        //then
        assertThat(WalletNftEntity.findById(WalletNftEntityId("123", "0xabc"))?.quantity, equalTo(9.toBigInteger()))
        assertThat(WalletNftEntity.findById(WalletNftEntityId("456", "0xabc")), nullValue())
        assertThat(WalletNftEntity.findById(WalletNftEntityId("123", "0xdef"))?.quantity, equalTo(BigInteger.ONE))
        assertThat(WalletNftEntity.findById(WalletNftEntityId("456", "0xdef"))?.quantity, equalTo(BigInteger.TEN))
    }
}
