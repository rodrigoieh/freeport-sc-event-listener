package nft.freeport.processor.cms

import nft.freeport.listener.event.SmartContractEvent
import nft.freeport.listener.event.TransferSingle
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.listener.position.dto.ProcessedEventPosition
import nft.freeport.listener.position.dto.ProcessingBlockState.PARTIALLY_DONE
import nft.freeport.processor.cms.nft.TransferSingleEventProcessor
import nft.freeport.processor.cms.price.SetExchangeRateEventProcessor
import nft.freeport.wrapEvent
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigInteger

class CmsEventProcessorBaseTest {

    private val positionManager = mock<ProcessorsPositionManager>()
    private val transferSingleProcessor = mock<TransferSingleEventProcessor>()
    private val setExchangeRateProcessor = mock<SetExchangeRateEventProcessor>()

    val testSubject: CmsEventProcessorBase = CmsEventProcessorBase(
        positionManager = positionManager, processorsMap = mapOf(
            "TransferSingle" to transferSingleProcessor as CmsEventProcessor<SmartContractEvent>,
            "SetExchangeRate" to setExchangeRateProcessor as CmsEventProcessor<SmartContractEvent>,
        )
    )

    @Test
    fun `some smart contract event -- related cms event processor is triggered`() {
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