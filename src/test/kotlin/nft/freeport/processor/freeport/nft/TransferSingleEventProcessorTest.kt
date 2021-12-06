package nft.freeport.processor.freeport.nft

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.CURRENCY_TOKEN_ID
import nft.freeport.ZERO_ADDRESS
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.TransferSingle
import nft.freeport.processor.freeport.contractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class TransferSingleEventProcessorTest {
    @Inject
    internal lateinit var testSubject: TransferSingleEventProcessor

    @Test
    fun `Supports TransferSingle event`() {
        assertThat(testSubject.supportedClass, equalTo(TransferSingle::class.java))
    }

    @Test
    @TestTransaction
    fun `Process NFT minted event`() {
        //given
        val event = TransferSingle(
            "0x123",
            "0x0000000000000000000000000000000000000000",
            "0xdef",
            "123",
            BigInteger.TEN
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        assertThat(NftEntity.findById("123"), notNullValue())
        assertThat(WalletNftEntity.findById(WalletNftEntityId("123", "0xdef"))?.quantity, equalTo(BigInteger.TEN))
    }

    @Test
    @TestTransaction
    fun `Process NFT transferred event`() {
        //given
        WalletNftEntity(WalletNftEntityId("123", "0xabc"), BigInteger.TEN).persist()
        val event = TransferSingle(
            "0x123",
            "0xabc",
            "0xdef",
            "123",
            BigInteger.ONE
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        assertThat(WalletNftEntity.findById(WalletNftEntityId("123", "0xabc"))?.quantity, equalTo(9.toBigInteger()))
        assertThat(WalletNftEntity.findById(WalletNftEntityId("123", "0xdef"))?.quantity, equalTo(BigInteger.ONE))
    }

    @Test
    @TestTransaction
    fun `Process currency transferred event`() {
        //given
        val event = TransferSingle(
            "0x123",
            ZERO_ADDRESS,
            "0xdef",
            CURRENCY_TOKEN_ID,
            BigInteger.ONE
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        assertThat(
            WalletNftEntity.findById(WalletNftEntityId(CURRENCY_TOKEN_ID, "0xdef"))?.quantity,
            equalTo(BigInteger.ONE)
        )
    }
}
