package nft.freeport.processor.ddc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import network.cere.ddc.client.producer.Producer
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

    private val testSubject = DdcProcessor(ddcConfig, objectMapper, ddcProducer)

    private val payloads = listOf(
        TransferSingle("0x0", "0x1", "0x2", "0x3", BigInteger.TEN),
        TransferBatch(
            "0x0",
            "0x1",
            "0x2",
            listOf("0x3", "0x4"),
            listOf(BigInteger.TEN, BigInteger.ONE)
        ),
        JointAccountShareCreated("0x0", "0x1", 10000),
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
        SetExchangeRate(BigInteger.TEN),
        StartAuction("0x0", "0x1", BigInteger.TEN, BigInteger.ONE),
        BidOnAuction("0x0", "0x1", BigInteger.TEN, BigInteger.ONE, "0x2"),
        SettleAuction("0x0", "0x1", BigInteger.TEN, "0x2"),
        AttachToNFT("0x0", "0x1", "0x2"),
    )

    @Test
    fun `Processor ID is 2`() {
        assertThat(testSubject.id, equalTo(2))
    }

    @Test
    fun `Process events`() {
        payloads.mapIndexed { i, e ->
            EventEntity(
                id = i.toLong(),
                name = e::class.java.simpleName,
                payload = objectMapper.writeValueAsString(e),
                timestamp = Instant.now(),
                txHash = "0xcafebabe"
            )
        }.forEach(testSubject::process)
        verify(ddcProducer, times(10)).send(any())
    }
}