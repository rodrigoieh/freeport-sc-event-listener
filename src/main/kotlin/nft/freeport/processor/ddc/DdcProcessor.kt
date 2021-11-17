package nft.freeport.processor.ddc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import network.cere.ddc.client.producer.Piece
import network.cere.ddc.client.producer.Producer
import nft.freeport.listener.event.*
import nft.freeport.processor.EventProcessor
import org.slf4j.LoggerFactory
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DdcProcessor(
    ddcConfig: DdcConfig,
    private val objectMapper: ObjectMapper,
    private val ddcProducer: Producer
) : EventProcessor {
    private val log = LoggerFactory.getLogger(javaClass)

    private val pubKey = ddcConfig.pubKeyHex()

    override val id = 2

    private val supportedEvents = listOf(
        TransferSingle::class.java,
        TransferBatch::class.java,
        RoyaltiesConfigured::class.java,
        MakeOffer::class.java,
        TakeOffer::class.java,
        StartAuction::class.java,
        BidOnAuction::class.java,
        SettleAuction::class.java,
        AttachToNFT::class.java,
    ).associateBy { it.simpleName }

    override fun process(e: EventEntity) {
        val eventType = supportedEvents[e.name] ?: return
        when (eventType) {
            TransferBatch::class.java -> processBatch(e)
            else -> uploadToDdc(objectMapper.readTree(e.payload).get("nftId").textValue(), e)
        }
    }

    private fun processBatch(e: EventEntity) {
        objectMapper.readValue<TransferBatch>(e.payload)
            .convertToSingle()
            .forEach { uploadToDdc(it.nftId, e) }
    }

    private fun uploadToDdc(nftId: String, e: EventEntity) {
        log.info("Uploading event {} to DDC", e.payload)
        val piece = Piece().apply {
            id = UUID.randomUUID().toString()
            appPubKey = pubKey
            userPubKey = nftId
            timestamp = e.timestamp
            data = e.payload
        }
        uploadToDdc(piece)
    }

    private fun uploadToDdc(piece: Piece) {
        ddcProducer.send(piece).await().indefinitely()
    }
}
