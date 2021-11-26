package nft.freeport.processor.ddc

import com.fasterxml.jackson.databind.ObjectMapper
import network.cere.ddc.client.producer.Piece
import network.cere.ddc.client.producer.Producer
import nft.freeport.DDC_PROCESSOR_ID
import nft.freeport.SMART_CONTRACT_EVENTS_DDC_TOPIC_NAME
import nft.freeport.listener.event.*
import nft.freeport.listener.processorsPosition.ProcessorsPositionManager
import nft.freeport.processor.EventProcessor
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import javax.transaction.Transactional
import kotlin.reflect.KClass

/**
 * TODO
 *  is it possible to provide cid from out side? Hash from data maybe.
 *  to prevent data duplicates in case of failing after publishing event to ddc but before saving position in the db.
 */
class DdcProcessor(
    private val objectMapper: ObjectMapper,
    private val ddcProducer: Producer,

    ddcConfig: DdcConfig,
    stateProvider: ProcessorsPositionManager,
) : EventProcessor(stateProvider) {
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

    // to override channel name, because ddc works in the separate one.
    // we decided to do it because of ddc latency, so it can slow down other consumers of events
    @Incoming(SMART_CONTRACT_EVENTS_DDC_TOPIC_NAME)
    @Transactional
    override fun processAndCommit(eventData: SmartContractEventData<out SmartContractEvent>) =
        super.processAndCommit(eventData)

    override fun process(eventData: SmartContractEventData<out SmartContractEvent>) {
        when (eventData.event) {
            is TransferBatch -> processBatch(eventData.event, eventData)
            // todo fix getting id via models
            else -> uploadToDdc(
                nftId = objectMapper.readTree(objectMapper.writeValueAsString(eventData.event)).get("nftId")
                    .textValue(),
                eventData = eventData
            )
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
            id = UUID.randomUUID().toString()
            appPubKey = pubKey
            userPubKey = nftId
            timestamp = Instant.parse(rawEvent.blockSignedAt)
            data = objectMapper.writeValueAsString(event)
        }

        ddcProducer.send(piece).await().indefinitely()
    }
}
