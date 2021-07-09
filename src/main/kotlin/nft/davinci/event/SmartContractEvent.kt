package nft.davinci.event

import java.math.BigInteger

sealed class SmartContractEvent(
    open val blockSignedAt: String,
    open val txHash: String
)

data class TransferSingle(
    override val blockSignedAt: String,
    override val txHash: String,
    val operator: String,
    val from: String,
    val to: String,
    val id: String,
    val amount: BigInteger
) : SmartContractEvent(blockSignedAt, txHash)

data class TransferBatch(
    override val blockSignedAt: String,
    override val txHash: String,
    val operator: String,
    val from: String,
    val to: String,
    val ids: List<String>,
    val amounts: List<BigInteger>
) : SmartContractEvent(blockSignedAt, txHash)

data class JointAccountShareCreated(
    override val blockSignedAt: String,
    override val txHash: String,
    val account: String,
    val owner: String,
    val fraction: Int
) : SmartContractEvent(blockSignedAt, txHash)
