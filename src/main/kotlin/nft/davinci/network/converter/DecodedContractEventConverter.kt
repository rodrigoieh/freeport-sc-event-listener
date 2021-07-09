package nft.davinci.network.converter

import nft.davinci.event.SmartContractEvent
import nft.davinci.network.dto.ContractEvent

interface DecodedContractEventConverter<T : SmartContractEvent> {
    val supportedClass: Class<T>

    fun convert(source: ContractEvent): T
}
