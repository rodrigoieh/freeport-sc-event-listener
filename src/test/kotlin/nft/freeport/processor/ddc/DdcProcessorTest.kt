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

    private val testSubject = DdcProcessor(objectMapper, ddcProducer, ddcConfig, stateProvider)

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
        ) to "0xf66798ac3053788ae439647a8012762b623aa3b6de0b2ddd69c3188fa64e0e2c",
        RoyaltiesConfigured(
            "0x0",
            "0x1",
            10000,
            BigInteger.TEN,
            "0x2",
            10000,
            BigInteger.ONE
        ) to "0x5c192e9bdd46c804423b090029ba9e60e53aa70fa1886347e33eefbddc29c5ae",
        MakeOffer(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN
        ) to "0x130ea0788be98e6fb3daec4e407c72ac61b96b3c2c3696282f1761b96d11ceb1",
        TakeOffer(
            buyer = "0x0",
            seller = "0x1",
            nftId = "0x2",
            price = BigInteger.TEN,
            amount = BigInteger.ONE
        ) to "0xb980b3de183b2042f6c93e694b0efee88f906799af241bc13e3f9f35fdc752b1",
        StartAuction(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN,
            closeTimeSec = BigInteger.ONE
        ) to "0x5a2e903bdeac17fc49173103328841e320d793c80daa6566dece9edc7c8b7cd5",
        BidOnAuction(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN,
            closeTimeSec = BigInteger.ONE,
            buyer = "0x2"
        ) to "0x6829e1d09debfa52842f36079b730218570f5d0aa5638b6a333353de782a4a2d",
        SettleAuction(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN,
            buyer = "0x2"
        ) to "0x46e5081cdc36cd67853f5b9fb801b61c3879190daa743817ea0ce0aa9a900931",
        AttachToNFT(
            sender = "0x0",
            nftId = "0x1",
            cid = "0x2"
        ) to "0xf576bab5b77ededb81057bd9d77ae400e9db83561e6eea0c9aefddd9bb6507b7",
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