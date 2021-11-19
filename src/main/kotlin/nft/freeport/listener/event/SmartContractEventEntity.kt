package nft.freeport.listener.event

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "sc_events")
class SmartContractEventEntity(
    @Id
    @SequenceGenerator(
        name = "scEventsSeq",
        sequenceName = "sc_events_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scEventsSeq")
    val id: Long?,

    @Column
    val name: String,

    @Column
    val payload: String,

    @Column
    val timestamp: Instant,

    @Column(name = "tx_hash")
    val txHash: String
) : PanacheEntityBase