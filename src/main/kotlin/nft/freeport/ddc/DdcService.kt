package nft.freeport.ddc

import com.fasterxml.jackson.databind.ObjectMapper
import network.cere.ddc.client.producer.Piece
import network.cere.ddc.client.producer.Producer
import nft.freeport.event.NftEvent
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DdcService(
    private val ddcConfig: DdcConfig,
    private val ddcProducer: Producer,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendNftEvent(nftEvent: NftEvent, blockSignedAt: String, txHash: String) {
        log.info("Storing {} event in DDC", nftEvent.eventType())
        val event = DdcNftEvent(nftEvent.eventType(), txHash, blockSignedAt, nftEvent)
        val piece = Piece().apply {
            id = txHash
            appPubKey = ddcConfig.pubKeyHex()
            userPubKey = nftEvent.operator
            timestamp = Instant.parse(blockSignedAt)
            data = objectMapper.writeValueAsString(event)
        }
        uploadToDdc(piece)
        uploadToDdc(piece.copy(
            id = "$txHash-${nftEvent.nftId}",
            userPubKey = nftEvent.nftId
        ))
    }

    private fun uploadToDdc(piece: Piece) {
        ddcProducer.send(piece).await().indefinitely()
    }
}
