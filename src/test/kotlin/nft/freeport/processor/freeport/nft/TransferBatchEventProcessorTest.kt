package nft.freeport.processor.freeport.nft

import io.kotest.matchers.shouldBe
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferBatch
import nft.freeport.processor.freeport.contractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class TransferBatchEventProcessorTest {
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
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        WalletNftEntity.findById(WalletNftEntityId("123", "0xabc"))?.quantity shouldBe 9.toBigInteger()
        WalletNftEntity.findById(WalletNftEntityId("456", "0xabc"))?.quantity shouldBe BigInteger.ZERO
        WalletNftEntity.findById(WalletNftEntityId("123", "0xdef"))?.quantity shouldBe BigInteger.ONE
        WalletNftEntity.findById(WalletNftEntityId("456", "0xdef"))?.quantity shouldBe BigInteger.TEN
    }
}
