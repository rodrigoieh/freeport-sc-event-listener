package nft.davinci.event

import java.math.BigInteger

sealed class SmartContractEvent

data class TransferSingle(
    val operator: String,
    val from: String,
    val to: String,
    val id: String,
    val amount: BigInteger
) : SmartContractEvent()

data class TransferBatch(
    val operator: String,
    val from: String,
    val to: String,
    val ids: List<String>,
    val amounts: List<BigInteger>
) : SmartContractEvent()

data class JointAccountShareCreated(
    val account: String,
    val owner: String,
    val fraction: BigInteger
) : SmartContractEvent()
