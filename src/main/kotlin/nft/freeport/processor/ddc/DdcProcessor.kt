package nft.freeport.processor.ddc

import com.fasterxml.jackson.databind.ObjectMapper
import network.cere.ddc.client.producer.Piece
import network.cere.ddc.client.producer.Producer
import nft.freeport.DDC_PROCESSOR_ID
import nft.freeport.listener.event.*
import nft.freeport.listener.position.ProcessorsPositionManager
import nft.freeport.listener.position.entity.ProcessorLastScannedEventPositionEntity
import nft.freeport.processor.EventProcessor
import org.komputing.khex.extensions.toHexString
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import kotlin.reflect.KClass

@ApplicationScoped
class DdcProcessor(
    override val stateProvider: ProcessorsPositionManager,
    private val objectMapper: ObjectMapper,
    private val ddcProducer: Producer,

    ddcConfig: DdcConfig,
) : EventProcessor {
    private val log = LoggerFactory.getLogger(javaClass)
    private val pubKey = ddcConfig.pubKeyHex()

    override val id = DDC_PROCESSOR_ID
    override val supportedEvents: Set<KClass<out SmartContractEvent>> = setOf(
        TransferSingle::class,
        TransferBatch::class,
        RoyaltiesConfigured::class,
        MakeOffer::class,
        TakeOffer::class,
        StartAuction::class,
        BidOnAuction::class,
        SettleAuction::class,
        AttachToNFT::class,
    )

    override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {
        when (eventData.event) {
            is TransferBatch -> processBatch(eventData.event, eventData)
            is NftRelatedEvent -> uploadToDdc(nftId = eventData.event.nftId, eventData = eventData)
            else -> error("An unexpected smart contract event type, it can't be uploaded to ddc. The type is ${eventData.event::class}")
        }
    }

    private fun processBatch(batch: TransferBatch, eventData: SmartContractEventData<out SmartContractEvent>) {
        batch.convertToSingle()
            .forEach { uploadToDdc(it.nftId, eventData) }
    }

    private fun uploadToDdc(
        nftId: String,
        eventData: SmartContractEventData<out SmartContractEvent>
    ) = with(eventData) {
        log.info("Uploading event {} to DDC", event)

        val piece = Piece().apply {
            id = deriveIdFromEvent(event)
            appPubKey = pubKey
            userPubKey = nftId
            timestamp = Instant.parse(rawEvent.blockSignedAt)
            data = objectMapper.writeValueAsString(event)
        }

        ddcProducer.send(piece).await().indefinitely()
    }

    /**
     * To prevent data duplication in case of app failure after sending event to ddc, but before committing state [ProcessorLastScannedEventPositionEntity].
     */
    private fun deriveIdFromEvent(event: SmartContractEvent): String {
        val digest = MessageDigest.getInstance("SHA3-256")
        digest.update(objectMapper.writeValueAsBytes(event))
        return digest.digest().toHexString()
    }
}
