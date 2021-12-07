package nft.freeport.processor.ddc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import network.cere.ddc.client.producer.Producer
import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.listener.event.*
import nft.freeport.listener.position.ProcessorsPositionManager
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.kotlin.*
import java.math.BigInteger
import java.time.Instant

internal class DdcProcessorTest {
    private val ddcConfig = object : DdcConfig {
        override fun enabled() = true
        override fun bootNodes() = listOf("")
        override fun pubKeyHex() = "0xcafebabe"
        override fun secKeyHex() = "0xcafebabe"
    }
    private val objectMapper = jacksonObjectMapper()
    private val ddcProducer = mock<Producer>()
    private val stateProvider = mock<ProcessorsPositionManager>()

    private val testSubject = DdcProcessor(stateProvider, objectMapper, ddcProducer, ddcConfig)

    private val payloads = listOf(
        TransferSingle("0x0", "0x1", "0x2", "0x3", BigInteger.TEN),
        TransferBatch(
            "0x0",
            "0x1",
            "0x2",
            listOf("0x3", "0x4"),
            listOf(BigInteger.TEN, BigInteger.ONE)
        ),
        RoyaltiesConfigured(
            "0x0",
            "0x1",
            10000,
            BigInteger.TEN,
            "0x2",
            10000,
            BigInteger.ONE
        ),
        MakeOffer("0x0", "0x1", BigInteger.TEN),
        TakeOffer("0x0", "0x1", "0x2", BigInteger.TEN, BigInteger.ONE),
        StartAuction("0x0", "0x1", BigInteger.TEN, BigInteger.ONE),
        BidOnAuction("0x0", "0x1", BigInteger.TEN, BigInteger.ONE, "0x2"),
        SettleAuction("0x0", "0x1", BigInteger.TEN, "0x2"),
        AttachToNFT("0x0", "0x1", "0x2"),
    )
    val mockedRawEvent = ContractEvent(
        blockSignedAt = Instant.now().toString(),
        blockHeight = 0,
        txHash = "0xcafebabe",
        rawLogTopics = emptyList(),
        rawLogData = "",
        decoded = null,
        logOffset = 0
    )

    @Test
    fun `Processor ID is ddc`() {
        assertThat(testSubject.id, equalTo("ddc"))
    }

    @Test
    fun `Process events`() {
        payloads.mapIndexed { _, e ->
            SmartContractEventData(contract = "some-contract", event = e, rawEvent = mockedRawEvent)
        }.forEach(testSubject::process)
        verify(ddcProducer, times(10)).send(any())
    }

    @TestFactory
    fun `cid should be derived from event itself`(): List<DynamicTest> = listOf(
        TransferSingle(
            operator = "0x0",
            from = "0x1",
            to = "0x2",
            nftId = "0x3",
            amount = BigInteger.TEN
        ) to "0x56f32c016953e988f0e34c8022c8fd4312a4e04c997d34dc14d19f9b4406fcaa",
        RoyaltiesConfigured(
            "0x0",
            "0x1",
            10000,
            BigInteger.TEN,
            "0x2",
            10000,
            BigInteger.ONE
        ) to "0x7cf0aa3c65c40d476a353ce9dab17f841b3a55e4cf67e15817f17409b692f3bd",
        MakeOffer(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN
        ) to "0x1c1a38c8fab5af5418d2b995e30d6008d9b371d2ac6726975a7ac14d3869279d",
        TakeOffer(
            buyer = "0x0",
            seller = "0x1",
            nftId = "0x2",
            price = BigInteger.TEN,
            amount = BigInteger.ONE
        ) to "0x1753389960889e2377ecee7deac8f296c032ccc8f8f7ade7973243cdeaf9d0e7",
        StartAuction(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN,
            closeTimeSec = BigInteger.ONE
        ) to "0xc639d4ba40b7facbb5d4299f80f6d3e3fb2be6ed5b1521d07b9fec2f722d963c",
        BidOnAuction(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN,
            closeTimeSec = BigInteger.ONE,
            buyer = "0x2"
        ) to "0xef63ed570c68ec1804ef0fd187a2081c6425983638494164578b71361949e2d1",
        SettleAuction(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN,
            buyer = "0x2"
        ) to "0xe24ea69b242a46ed9087d066cd43bd15f13316df7e74bba5ae8ab9b724c8dfa1",
        AttachToNFT(
            sender = "0x0",
            nftId = "0x1",
            cid = "0x2"
        ) to "0x706396e5af6f8b67873c034c3aa5aa33bc878bf0ebd5606f2ae0ed0885d37c65",
    ).map { (event: SmartContractEvent, hashedId) ->
        DynamicTest.dynamicTest("$event id should be $hashedId") {
            clearInvocations(ddcProducer)

            testSubject.process(
                SmartContractEventData(contract = "some-contract", event = event, rawEvent = mockedRawEvent)
            )

            verify(ddcProducer) {
                1 * {
                    send(argForWhich { id == hashedId })
                }
            }
        }
    }
}