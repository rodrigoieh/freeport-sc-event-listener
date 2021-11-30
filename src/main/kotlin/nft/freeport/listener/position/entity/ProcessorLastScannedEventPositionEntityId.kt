package nft.freeport.listener.position.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ProcessorLastScannedEventPositionEntityId(
    @Column(name = "processor_id")
    val processorId: String,

    @Column(name = "contract")
    val contract: String,
) : Serializable