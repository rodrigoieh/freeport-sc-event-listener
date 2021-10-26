package nft.freeport.ddc

import io.smallrye.mutiny.Uni
import network.cere.ddc.client.producer.Piece
import network.cere.ddc.client.producer.Producer
import network.cere.ddc.client.producer.SendPieceResponse
import org.slf4j.LoggerFactory
import java.util.*

class NoOpDdcProducer : Producer {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(piece: Piece): Uni<SendPieceResponse> {
        log.info("Mock sending piece to DDC")
        return Uni.createFrom().item(SendPieceResponse(UUID.randomUUID().toString()))
    }
}