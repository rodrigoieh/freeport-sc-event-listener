package nft.freeport.listener.position.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import nft.freeport.listener.position.dto.ProcessingBlockState
import javax.persistence.*

@Entity
@Table(name = "last_scanned_event_position_by_processor")
@IdClass(ProcessorLastScannedEventPositionEntityId::class)
class ProcessorLastScannedEventPositionEntity(

    @Id
    @Column(name = "processor_id")
    val processorId: String,

    @Id
    @Column(name = "contract")
    val contract: String,

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    var state: ProcessingBlockState,

    @Column(name = "block")
    var block: Long,

    /**
     * Offset of an event inside [block], one [block] can have multiple events.
     *
     * null means that we haven't handled any event from this block yet
     */
    @Column(name = "event_offset")
    var offset: Long?,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<ProcessorLastScannedEventPositionEntity, String>
}