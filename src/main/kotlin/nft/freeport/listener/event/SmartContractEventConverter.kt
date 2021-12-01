package nft.freeport.listener.event

import nft.freeport.listener.AbiDecoder
import nft.freeport.covalent.dto.ContractEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SmartContractEventConverter(private val abiDecoder: AbiDecoder) {
    private companion object {
        private const val PARAM_OPERATOR = "_operator"
        private const val PARAM_FROM = "_from"
        private const val PARAM_TO = "_to"
        private const val PARAM_ID = "_id"
        private const val PARAM_AMOUNT = "_amount"
        private const val PARAM_IDS = "_ids"
        private const val PARAM_AMOUNTS = "_amounts"
    }

    private val eventTopics = mapOf(
        "0xc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62" to TransferSingle::class,
        "0x4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb" to TransferBatch::class,
        "0x006fb9851f1fd2fbc9fa36680d17e1254999a38e5f3c76c3a1ecc126a464601b" to JointAccountShareCreated::class,
        "0x3df2272e42784a2455bbbdcdf25ae0f67d41f801896514cc4cd255aa8ee75e4c" to RoyaltiesConfigured::class,
        "0x040259e2f9c7930380b3a5c979ad8a30ecf8d344d3bcdb149e2c454ab85fcd8f" to MakeOffer::class,
        "0xe90359125940e7c9b26005d19d4d8a2a5335ea8bef094bcd61bb8f8091cad117" to TakeOffer::class,
        "0x1d5de90e7c5b244ac5797698b15fe80a92524d933dafd79e001daf844555fb1c" to SetExchangeRate::class,
        "0x5135842dc9522996ca3d92189d0ded7e70ecbfc5545c115def0c7bdb9ee41f2b" to StartAuction::class,
        "0x39e9b26db60de3ca88f045fdd8954028f1bbd0c6e2ff124121cb5a03da370191" to BidOnAuction::class,
        "0xfe2c1531a975fce0584787c5e2643df8c1fe92f870c9dbadb24e366e31e79f44" to SettleAuction::class,
        "0xcb0dbc631188ff7e4c5831ec907b2d9ca2786dd0314af3e43a7269821a19e2b4" to AttachToNFT::class,
    )

    private val converters = mapOf(
        TransferSingle::class to ::convertTransferSingle,
        TransferBatch::class to ::convertTransferBatch,
        JointAccountShareCreated::class to ::convertJointAccountShareCreated,
        RoyaltiesConfigured::class to ::convertRoyaltiesConfigured,
        MakeOffer::class to ::convertMakeOffer,
        TakeOffer::class to ::convertTakeOffer,
        SetExchangeRate::class to ::convertSetExchangeRate,
        StartAuction::class to ::convertStartAuction,
        BidOnAuction::class to ::convertBidOnAuction,
        SettleAuction::class to ::convertSettleAuction,
        AttachToNFT::class to ::convertAttachToNFT,
    )

    fun convert(source: ContractEvent): SmartContractEvent? {
        return source.rawLogTopics.firstOrNull()
            ?.let(eventTopics::get)
            ?.let(converters::get)
            ?.invoke(source)
    }

    private fun convertTransferSingle(source: ContractEvent): TransferSingle {
        requireNotNull(source.decoded)
        return TransferSingle(
            source.decoded.getParamStringValue(PARAM_OPERATOR),
            source.decoded.getParamStringValue(PARAM_FROM),
            source.decoded.getParamStringValue(PARAM_TO),
            source.decoded.getParamStringValue(PARAM_ID),
            source.decoded.getParamStringValue(PARAM_AMOUNT).toBigInteger()
        )
    }

    private fun convertTransferBatch(source: ContractEvent): TransferBatch {
        requireNotNull(source.decoded)
        return TransferBatch(
            source.decoded.getParamStringValue(PARAM_OPERATOR),
            source.decoded.getParamStringValue(PARAM_FROM),
            source.decoded.getParamStringValue(PARAM_TO),
            source.decoded.getParamArrayValues(PARAM_IDS),
            source.decoded.getParamArrayValues(PARAM_AMOUNTS).map(String::toBigInteger)
        )
    }

    private fun convertJointAccountShareCreated(source: ContractEvent): JointAccountShareCreated {
        requireNotNull(source.rawLogData)
        return JointAccountShareCreated(
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeAddress(source.rawLogTopics[2]),
            abiDecoder.decodeUint256(source.rawLogData).toInt()
        )
    }

    private fun convertRoyaltiesConfigured(source: ContractEvent): RoyaltiesConfigured {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return RoyaltiesConfigured(
            abiDecoder.decodeUint256(source.rawLogTopics[1]).toString(),
            abiDecoder.decodeAddress(output[0]),
            abiDecoder.decodeUint256(output[1]).toInt(),
            abiDecoder.decodeUint256(output[2]),
            abiDecoder.decodeAddress(output[3]),
            abiDecoder.decodeUint256(output[4]).toInt(),
            abiDecoder.decodeUint256(output[5])
        )
    }

    private fun convertMakeOffer(source: ContractEvent): MakeOffer {
        requireNotNull(source.rawLogData)
        return MakeOffer(
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeUint256(source.rawLogData)
        )
    }

    private fun convertTakeOffer(source: ContractEvent): TakeOffer {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return TakeOffer(
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeAddress(source.rawLogTopics[2]),
            abiDecoder.decodeUint256(source.rawLogTopics[3]).toString(),
            abiDecoder.decodeUint256(output[0]),
            abiDecoder.decodeUint256(output[1])
        )
    }

    private fun convertSetExchangeRate(source: ContractEvent): SetExchangeRate {
        requireNotNull(source.rawLogData)
        return SetExchangeRate(abiDecoder.decodeUint256(source.rawLogData))
    }

    private fun convertStartAuction(source: ContractEvent): StartAuction {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return StartAuction(
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeUint256(output[0]),
            abiDecoder.decodeUint256(output[1]),
        )
    }

    private fun convertBidOnAuction(source: ContractEvent): BidOnAuction {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return BidOnAuction(
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeUint256(output[0]),
            abiDecoder.decodeUint256(output[1]),
            abiDecoder.decodeAddress(output[2])
        )
    }

    private fun convertSettleAuction(source: ContractEvent): SettleAuction {
        requireNotNull(source.rawLogData)
        val output = source.rawLogData.substring(2).chunked(64)
        return SettleAuction(
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeUint256(output[0]),
            abiDecoder.decodeAddress(output[1])
        )
    }

    private fun convertAttachToNFT(source: ContractEvent): AttachToNFT {
        requireNotNull(source.rawLogData)
        return AttachToNFT(
            abiDecoder.decodeAddress(source.rawLogTopics[1]),
            abiDecoder.decodeUint256(source.rawLogTopics[2]).toString(),
            abiDecoder.decodeCid(source.rawLogData),
        )
    }
}