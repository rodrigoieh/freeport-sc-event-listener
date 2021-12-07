package nft.freeport.listener

import com.github.tomakehurst.wiremock.WireMockServer
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import nft.freeport.FREEPORT_PROCESSOR_ID
import nft.freeport.listener.config.ContractConfig
import nft.freeport.listener.config.ContractsConfig
import nft.freeport.listener.event.BlockProcessedEvent
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.listener.position.dto.ProcessedEventPosition
import nft.freeport.listener.position.dto.ProcessingBlockState.NEW
import nft.freeport.processor.freeport.FreeportEventProcessorBase
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito
import org.mockito.kotlin.*
import javax.inject.Inject


@QuarkusTest
@TestInstance(PER_CLASS)
@QuarkusTestResource(WiremockCovalent::class)
class SmartContractEventsReaderTest {

    @field:InjectCovalentWiremock
    private lateinit var wireMockServer: WireMockServer

    @Inject
    private lateinit var testSubject: SmartContractEventsReader

    @InjectMock
    private lateinit var positionManager: ProcessorsPositionManager

    @InjectMock
    private lateinit var freeportEventProcessor: FreeportEventProcessorBase

    @InjectMock
    private lateinit var contractsConfig: ContractsConfig

    @BeforeAll
    fun setUp() {
        whenever(contractsConfig.contracts()).thenReturn(mapOf(
            TEST_CONTRACT to object : ContractConfig {
                override fun address(): String = TEST_CONTRACT_ADDRESS

                /** doesn't matter, [positionManager] is mocked */
                override fun firstBlockNumber(): Long = 0L
            }
        ))
    }

    companion object {
        const val TEST_CONTRACT = "test-contract"
        const val TEST_CONTRACT_ADDRESS = "0x000000001"
    }

    @Test
    fun `covalent returns 100 events (means that there are more, but we've reached the limit) -- trying to split initial requests into sub-requests (from,to) with return less than 100 events`() {
        // given
        val startBlockNumber = 1000L
        whenever(positionManager.getCurrentPosition(eq(FREEPORT_PROCESSOR_ID), eq(TEST_CONTRACT_ADDRESS))).thenReturn(
            ProcessedEventPosition(block = startBlockNumber, offset = null, currentState = NEW)
        )
        wireMockServer.stubGettingLatestBlock(block = 1500)

        // mock first request, trying to get all events 1000..1500 blocks
        wireMockServer.stubGettingEvents(contract = TEST_CONTRACT_ADDRESS, from = startBlockNumber, to = 1501) {
            // return 100 events, it means that we've reached to limit
            generateOrderedEmptyEvents(
                startEventNumber = 1, lastEventNumber = 100,
                startBlockNumber = startBlockNumber, eventsPerBlock = 1,
            )
        }
        // then we should go deeper recursively using binary search to find [from..to] pair with less than 100 events
        //              1000..1500 - got 100, KO, trying to split it into two requests
        // 1) 1000..1251 - got 50, OK, send to processor     2) 1250..1500 - got 50, OK, send to processor

        // 1) mock trying to get events 1000..1250 blocks
        wireMockServer.stubGettingEvents(contract = TEST_CONTRACT_ADDRESS, from = startBlockNumber, to = 1251) {
            generateOrderedEmptyEvents(
                startEventNumber = 1, lastEventNumber = 50,
                startBlockNumber = startBlockNumber, eventsPerBlock = 1,
            )
        }
        // 2) mock trying to get events 1251..1500 blocks
        wireMockServer.stubGettingEvents(contract = TEST_CONTRACT_ADDRESS, from = 1250, to = 1501) {
            generateOrderedEmptyEvents(
                startEventNumber = 51, lastEventNumber = 100,
                startBlockNumber = startBlockNumber, eventsPerBlock = 1,
            )
        }

        val orderVerifier = Mockito.inOrder(freeportEventProcessor)
        // start flow
        testSubject.freeportProcessor()

        /**
         * Verify that all 100 events passed in the right order.
         * Events aren't of normal type, so they will be converted to null, but every block has such an event
         * so, we will pass [BlockProcessedEvent] once for every block.
         *
         * Right order of [BlockProcessedEvent] events means the right order of initial events which are mapped to nulls.
         */
        (1..100).forEach {
            orderVerifier.verify(freeportEventProcessor).processAndCommit(argThat {
                this.rawEvent.blockHeight == startBlockNumber + it
            })
        }
    }

    @Test
    fun `more than million blocks are requested -- batch processing with one million step should be used`() {
        // given
        val startBlockNumber = 1000L
        val endBlockNumber = startBlockNumber + 2_500_000

        whenever(positionManager.getCurrentPosition(eq(FREEPORT_PROCESSOR_ID), eq(TEST_CONTRACT_ADDRESS))).thenReturn(
            ProcessedEventPosition(block = startBlockNumber, offset = null, currentState = NEW)
        )
        wireMockServer.stubGettingLatestBlock(block = endBlockNumber)

        // mock first request, step is one million
        wireMockServer.stubGettingEvents(
            contract = TEST_CONTRACT_ADDRESS,
            from = 1000L,
            to = 1_001_000L,
        ) {
            generateOrderedEmptyEvents(
                startEventNumber = 1, lastEventNumber = 49,
                startBlockNumber = startBlockNumber, eventsPerBlock = 1,
            )
        }

        // mock second request, additional step in one million
        wireMockServer.stubGettingEvents(
            contract = TEST_CONTRACT_ADDRESS,
            from = 1_001_000L,
            to = 2_000_999L,
        ) {
            generateOrderedEmptyEvents(
                startEventNumber = 50, lastEventNumber = 100,
                startBlockNumber = 1_001_000L, eventsPerBlock = 1,
            )
        }

        // start flow
        testSubject.freeportProcessor()

        // check that real requests matches to the previous mocks
        verify(freeportEventProcessor, times(100)).processAndCommit(any())
    }
}