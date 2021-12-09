package nft.freeport.processor.freeport

import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.TransferSingle
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.listener.position.dto.ProcessedEventPosition
import nft.freeport.listener.position.dto.ProcessingBlockState.PARTIALLY_DONE
import nft.freeport.processor.freeport.nft.TransferSingleEventProcessor
import nft.freeport.processor.freeport.price.SetExchangeRateEventProcessor
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigInteger

class FreeportEventProcessorBaseTest {

    private val positionManager = mock<ProcessorsPositionManager>()
    private val transferSingleProcessor = mock<TransferSingleEventProcessor>()
    private val setExchangeRateProcessor = mock<SetExchangeRateEventProcessor>()

    val testSubject: FreeportEventProcessorBase = FreeportEventProcessorBase(
        positionManager = positionManager, processorsMap = mapOf(
            "TransferSingle" to transferSingleProcessor as FreeportEventProcessor<SmartContractEvent>,
            "SetExchangeRate" to setExchangeRateProcessor as FreeportEventProcessor<SmartContractEvent>,
        )
    )

    @Test
    fun `some smart contract event -- related freeport event processor is triggered`() {
        val eventData = TransferSingle(
            operator = "operator", from = "0x1", to = "0x2",
            nftId = "nft-id", amount = BigInteger.ONE
        ).wrapEvent(block = 101)

        whenever(positionManager.getCurrentPosition(testSubject.id, eventData.contract)).thenReturn(
            ProcessedEventPosition(block = 100, offset = null, currentState = PARTIALLY_DONE)
        )

        testSubject.processAndCommit(eventData = eventData)

        verify(transferSingleProcessor).process(eq(eventData))
    }
}