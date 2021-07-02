package nft.davinci.ddc

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.Startup
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import network.cere.ddc.client.producer.DdcProducer
import network.cere.ddc.client.producer.Piece
import nft.davinci.network.dto.NftEvent
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@Suppress("BlockingMethodInNonBlockingContext")
@ApplicationScoped
@Startup
class DdcService(
    private val ddcConfig: DdcConfig,
    private val ddcProducer: DdcProducer,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun sendNftEvent(nftEvent: NftEvent) = coroutineScope {
        log.info("Storing {} event in DDC", nftEvent.eventType())
        val event = DdcNftEvent(nftEvent.eventType(), nftEvent)
        val piece = Piece().apply {
            id = UUID.randomUUID().toString()
            appPubKey = ddcConfig.pubKeyHex()
            userPubKey = nftEvent.operator
            timestamp = Instant.now()
            data = objectMapper.writeValueAsString(event)
        }
        uploadToDdcAsync(piece).await()
    }

    private suspend fun uploadToDdcAsync(piece: Piece): Deferred<String> = coroutineScope {
        async { ddcProducer.send(piece).awaitSuspending().cid!! }
    }
}