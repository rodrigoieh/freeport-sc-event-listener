package nft.freeport.ddc

import com.fasterxml.jackson.databind.ObjectMapper
import network.cere.ddc.client.producer.Piece
import network.cere.ddc.client.producer.Producer
import nft.freeport.event.NftEvent
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DdcService(
    private val ddcConfig: DdcConfig,
    private val ddcProducer: Producer,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendNftEvent(nftEvent: NftEvent, blockSignedAt: String) {
        log.info("Storing {} event in DDC", nftEvent.eventType())
        val uuid = UUID.randomUUID().toString()
        val event = DdcNftEvent(nftEvent.eventType(), uuid, blockSignedAt, nftEvent)

        val piece = Piece().apply {
            id = uuid
            appPubKey = ddcConfig.pubKeyHex()
            userPubKey = nftEvent.operator
            timestamp = Instant.parse(blockSignedAt)
            data = objectMapper.writeValueAsString(event)
        }
        uploadToDdc(piece)
        uploadToDdc(
            piece.copy(
                id = "$uuid-${nftEvent.nftId}",
                userPubKey = nftEvent.nftId
            )
        )
    }

    private fun uploadToDdc(piece: Piece) {
        ddcProducer.send(piece).await().indefinitely()
    }
}
