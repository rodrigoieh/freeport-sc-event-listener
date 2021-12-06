package nft.freeport.processor

import nft.freeport.listener.event.*
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.listener.position.dto.ProcessedEventPosition
import nft.freeport.listener.position.dto.ProcessingBlockState.*
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.math.BigInteger
import kotlin.reflect.KClass

class EventProcessorTest {

    private val positionManager = mock<ProcessorsPositionManager>()
    private val testSubject: EventProcessor = spy(TestEventProcessor(positionManager = positionManager))

    @Test
    fun `event position is before actual position (by block) -- event should be skipped`() {
        val eventData = TransferSingle(
            operator = "operator", from = "0x1", to = "0x2",
            nftId = "nft-id", amount = BigInteger.ONE
        ).wrapEvent(block = 99)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = null, currentState = PARTIALLY_DONE)
        )

        testSubject.processAndCommit(eventData)

        verify(positionManager) { 0 * { updatePosition(any()) } }
        verify(testSubject) { 0 * { process(eq(eventData)) } }
    }

    @Test
    fun `event position is after actual position (by block) -- event is processed, position is updated`() {
        val eventData = TransferSingle(
            operator = "operator", from = "0x1", to = "0x2",
            nftId = "nft-id", amount = BigInteger.ONE
        ).wrapEvent(block = 101)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = null, currentState = PARTIALLY_DONE)
        )

        testSubject.processAndCommit(eventData)

        verify(positionManager) {
            1 * { updatePosition(argThat { block == eventData.rawEvent.blockHeight && state == PARTIALLY_DONE }) }
        }
        verify(testSubject) { 1 * { process(eq(eventData)) } }
    }

    @Test
    fun `event type is not supported -- event should be skipped, position should be updated`() {
        val eventData = SetExchangeRate(cereUnitsPerPenny = BigInteger.TEN).wrapEvent(block = 101)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = null, currentState = PARTIALLY_DONE)
        )

        testSubject.processAndCommit(eventData)

        verify(testSubject) { 0 * { process(eq(eventData)) } }
        verify(positionManager) {
            1 * { updatePosition(argThat { block == eventData.rawEvent.blockHeight && state == PARTIALLY_DONE }) }
        }
    }

    @Test
    fun `BlockProcessed event is passed -- actual position should be updated, event should be skipped`() {
        val eventData = BlockProcessedEvent.wrapEvent(block = 100)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 99, offset = null, currentState = PARTIALLY_DONE)
        )

        testSubject.processAndCommit(eventData)

        verify(testSubject) { 0 * { process(eq(eventData)) } }
        verify(positionManager) {
            1 * {
                updatePosition(argThat {
                    block == eventData.rawEvent.blockHeight
                            && state == DONE
                            && contract == eventData.contract
                            && processorId == testSubject.id
                            && offset == eventData.rawEvent.logOffset
                })
            }
        }
    }

    @Test
    fun `event position is after actual position (by offset) -- event is processed, position is updated`() {
        val eventData = TransferSingle(
            operator = "operator", from = "0x1", to = "0x2",
            nftId = "nft-id", amount = BigInteger.ONE
        ).wrapEvent(block = 100, offset = 92)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = 15, currentState = PARTIALLY_DONE)
        )

        testSubject.processAndCommit(eventData)

        verify(positionManager) {
            1 * { updatePosition(argThat { block == eventData.rawEvent.blockHeight && state == PARTIALLY_DONE }) }
        }
        verify(testSubject) { 1 * { process(eq(eventData)) } }
    }

    @Test
    fun `event position is before actual position (by offset) -- event should be skipped, position not be updated`() {
        val eventData = TransferSingle(
            operator = "operator", from = "0x1", to = "0x2",
            nftId = "nft-id", amount = BigInteger.ONE
        ).wrapEvent(block = 100, offset = 15)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = 92, currentState = PARTIALLY_DONE)
        )

        testSubject.processAndCommit(eventData)

        verify(positionManager) { 0 * { updatePosition(any()) } }
        verify(testSubject) { 0 * { process(eq(eventData)) } }
    }

    @Test
    fun `event position is after actual position (by empty offset) -- event is processed, position is updated`() {
        val eventData = TransferSingle(
            operator = "operator", from = "0x1", to = "0x2",
            nftId = "nft-id", amount = BigInteger.ONE
        ).wrapEvent(block = 100, offset = 15)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = null, currentState = NEW)
        )

        testSubject.processAndCommit(eventData)

        verify(positionManager) {
            1 * { updatePosition(argThat { block == eventData.rawEvent.blockHeight && state == PARTIALLY_DONE && offset == eventData.rawEvent.logOffset }) }
        }
        verify(testSubject) { 1 * { process(eq(eventData)) } }
    }

    @Test
    fun `an event is related to the block with state DONE -- event should be skipped, position not be updated`() {
        val eventData = TransferSingle(
            operator = "operator", from = "0x1", to = "0x2",
            nftId = "nft-id", amount = BigInteger.ONE
        ).wrapEvent(block = 100, offset = 15)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = 50, currentState = DONE)
        )

        testSubject.processAndCommit(eventData)

        verify(positionManager) { 0 * { updatePosition(any()) } }
        verify(testSubject) { 0 * { process(eq(eventData)) } }
    }

    companion object {
        const val TEST_PROCESSOR_ID = "TEST-PROCESSOR"
    }

    open class TestEventProcessor(override val positionManager: ProcessorsPositionManager) : EventProcessor {
        override val id: String = TEST_PROCESSOR_ID
        override val supportedEvents: Set<KClass<out SmartContractEvent>> = setOf(
            TransferSingle::class
        )

        override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {}
    }

}