package nft.freeport.listener.event

import java.math.BigInteger

sealed class SmartContractEvent

interface NftRelatedEvent {
    val nftId: String
}

/**
 * Technical event, just to indicate that it's the last event of the block.
 */
object BlockProcessedEvent : SmartContractEvent()

data class TransferSingle(
    val operator: String,
    val from: String,
    val to: String,
    override val nftId: String,
    val amount: BigInteger
) : NftRelatedEvent, SmartContractEvent()

data class TransferBatch(
    val operator: String,
    val from: String,
    val to: String,
    val ids: List<String>,
    val amounts: List<BigInteger>
) : SmartContractEvent() {
    fun convertToSingle(): List<TransferSingle> {
        return ids.indices.map { TransferSingle(operator, from, to, ids[it], amounts[it]) }
    }
}

data class JointAccountShareCreated(
    val account: String,
    val owner: String,
    val fraction: Int
) : SmartContractEvent()

data class RoyaltiesConfigured(
    override val nftId: String,
    val primaryRoyaltyAccount: String,
    val primaryRoyaltyCut: Int,
    val primaryRoyaltyMinimum: BigInteger,
    val secondaryRoyaltyAccount: String,
    val secondaryRoyaltyCut: Int,
    val secondaryRoyaltyMinimum: BigInteger
) : NftRelatedEvent, SmartContractEvent()

data class MakeOffer(
    val seller: String,
    override val nftId: String,
    val price: BigInteger
) : NftRelatedEvent, SmartContractEvent()

data class TakeOffer(
    val buyer: String,
    val seller: String,
    override val nftId: String,
    val price: BigInteger,
    val amount: BigInteger
) : NftRelatedEvent, SmartContractEvent()

data class SetExchangeRate(
    val cereUnitsPerPenny: BigInteger
) : SmartContractEvent()

data class StartAuction(
    val seller: String,
    override val nftId: String,
    val price: BigInteger,
    val closeTimeSec: BigInteger
) : NftRelatedEvent, SmartContractEvent()

data class BidOnAuction(
    val seller: String,
    override val nftId: String,
    val price: BigInteger,
    val closeTimeSec: BigInteger,
    val buyer: String,
) : NftRelatedEvent, SmartContractEvent()

data class SettleAuction(
    val seller: String,
    override val nftId: String,
    val price: BigInteger,
    val buyer: String,
) : NftRelatedEvent, SmartContractEvent()

data class AttachToNFT(
    val sender: String,
    override val nftId: String,
    val cid: String
) : NftRelatedEvent, SmartContractEvent()
