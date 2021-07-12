package nft.davinci.network.converter

import nft.davinci.event.SmartContractEvent
import nft.davinci.network.dto.ContractEvent

interface ContractEventConverter<T : SmartContractEvent> {
    fun canConvert(source: ContractEvent): Boolean

    fun convert(source: ContractEvent): T
}
