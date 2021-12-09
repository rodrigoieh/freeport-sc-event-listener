package nft.freeport.listener.position

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.listener.position.dto.ProcessedEventPosition
import nft.freeport.listener.position.dto.ProcessingBlockState
import nft.freeport.listener.position.dto.ProcessingBlockState.DONE
import nft.freeport.listener.position.dto.ProcessingBlockState.PARTIALLY_DONE
import nft.freeport.listener.position.entity.ProcessorLastScannedEventPositionEntity
import org.junit.jupiter.api.*
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.NoResultException

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProcessorsPositionManagerTest {

    @Inject
    lateinit var positionManager: ProcessorsPositionManager

    @Test
    fun `contract is missed in both sources (db, configs) -- error`() {
        val processorId = "id"
        val contract = "0x01"

        val error = assertThrows<IllegalStateException> {
            positionManager.getCurrentPosition(processorId, contract)
        }

        error.message shouldContain processorId
        error.message shouldContain contract
    }

    @Test
    fun `contract is missed in db -- start position from configs should be used`() {
        positionManager.getCurrentPosition("test-processor", "0x02") shouldBe ProcessedEventPosition(
            block = 0, offset = null, currentState = ProcessingBlockState.NEW
        )
    }

    /**
     * to save data in db before state initializing [ProcessorsPositionManager.initLastScannedPositions].
     * [PostConstruct] is triggered after first access to [positionManager] in tests.
     */
    @Test
    @Order(1)
    @TestTransaction
    fun `contract presents in db -- data from db is used`() {
        val databaseProcessorId = "test-processor-from-db"
        val databaseContract = "0x03"

        ProcessorLastScannedEventPositionEntity.persist(
            ProcessorLastScannedEventPositionEntity(
                processorId = databaseProcessorId, contract = databaseContract,
                state = DONE, block = 42, offset = 24
            )
        )

        positionManager.getCurrentPosition(databaseProcessorId, databaseContract) shouldBe ProcessedEventPosition(
            block = 42, offset = 24, currentState = DONE
        )
    }

    @Test
    @TestTransaction
    fun `contract is updated -- it's saved to db`() {
        // given
        val processorId = "test-processor"
        val contract = "0x04"
        val entity = ProcessorLastScannedEventPositionEntity(
            processorId = processorId, contract = contract,
            state = PARTIALLY_DONE, block = 12, offset = 13,
        )

        // no data about contract
        assertThrows<NoResultException> {
            ProcessorLastScannedEventPositionEntity
                .find(
                    "processorId = ?1 and contract = ?2",
                    processorId,
                    contract
                )
                .singleResult()
        }

        // update
        positionManager.updatePosition(entity)

        // should be persisted
        ProcessorLastScannedEventPositionEntity
            .find(
                "processorId = ?1 and contract = ?2",
                processorId,
                contract
            )
            .singleResult() shouldBe entity
    }

}