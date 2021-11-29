package nft.freeport.processor.ddc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import network.cere.ddc.client.producer.Producer
import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.listener.processorsPosition.ProcessorsPositionManager
import nft.freeport.listener.event.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
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

    @Test
    fun `Processor ID is ddc`() {
        assertThat(testSubject.id, equalTo("ddc"))
    }

    @Test
    fun `Process events`() {
        payloads.mapIndexed { _, e ->
            SmartContractEventData(
                contract = "some-contract",
                event = e,
                rawEvent = ContractEvent(
                    blockSignedAt = Instant.now().toString(),
                    blockHeight = 0,
                    txHash = "0xcafebabe",
                    rawLogTopics = emptyList(),
                    rawLogData = "",
                    decoded = null,
                    logOffset = 0
                )
            )
        }.forEach(testSubject::process)
        verify(ddcProducer, times(10)).send(any())
    }
}