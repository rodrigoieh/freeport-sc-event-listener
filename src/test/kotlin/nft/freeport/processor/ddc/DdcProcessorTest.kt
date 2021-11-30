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
        MakeOffer(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN
        ) to "0xf66798ac3053788ae439647a8012762b623aa3b6de0b2ddd69c3188fa64e0e2c",
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
        ) to "0xf66798ac3053788ae439647a8012762b623aa3b6de0b2ddd69c3188fa64e0e2c",
        SettleAuction(
            seller = "0x0",
            nftId = "0x1",
            price = BigInteger.TEN,
            buyer = "0x2"
        ) to "0xf66798ac3053788ae439647a8012762b623aa3b6de0b2ddd69c3188fa64e0e2c",
    ).map { (event: SmartContractEvent, hashedId) ->
        DynamicTest.dynamicTest("$event id should be $hashedId") {
            testSubject.process(
                SmartContractEventData(contract = "some-contract", event = event, rawEvent = mockedRawEvent)
            )

            verify(ddcProducer) {
                1 * {
                    send(argForWhich {
                        id == hashedId
                    })
                }
            }
        }
    }
}