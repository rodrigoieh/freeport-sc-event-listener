package nft.freeport.listener.event

import java.math.BigInteger

sealed class SmartContractEvent

/**
 * Technical event, just to indicate that it's the last event of the block.
 */
object BlockProcessedEvent : SmartContractEvent()

data class TransferSingle(
    val operator: String,
    val from: String,
    val to: String,
    val nftId: String,
    val amount: BigInteger
) : SmartContractEvent()

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
    val nftId: String,
    val primaryRoyaltyAccount: String,
    val primaryRoyaltyCut: Int,
    val primaryRoyaltyMinimum: BigInteger,
    val secondaryRoyaltyAccount: String,
    val secondaryRoyaltyCut: Int,
    val secondaryRoyaltyMinimum: BigInteger
) : SmartContractEvent()

data class MakeOffer(
    val seller: String,
    val nftId: String,
    val price: BigInteger
) : SmartContractEvent()

data class TakeOffer(
    val buyer: String,
    val seller: String,
    val nftId: String,
    val price: BigInteger,
    val amount: BigInteger
) : SmartContractEvent()

data class SetExchangeRate(
    val cereUnitsPerPenny: BigInteger
) : SmartContractEvent()

data class StartAuction(
    val seller: String,
    val nftId: String,
    val price: BigInteger,
    val closeTimeSec: BigInteger
) : SmartContractEvent()

data class BidOnAuction(
    val seller: String,
    val nftId: String,
    val price: BigInteger,
    val closeTimeSec: BigInteger,
    val buyer: String,
) : SmartContractEvent()

data class SettleAuction(
    val seller: String,
    val nftId: String,
    val price: BigInteger,
    val buyer: String,
) : SmartContractEvent()

data class AttachToNFT(
    val sender: String,
    val nftId: String,
    val cid: String
) : SmartContractEvent()
