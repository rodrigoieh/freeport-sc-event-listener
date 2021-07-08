package nft.davinci.network.converter

import nft.davinci.event.SmartContractEvent
import nft.davinci.network.dto.DecodedContractEvent

interface DecodedContractEventConverter<T : SmartContractEvent> {
    val supportedClass: Class<T>

    fun convert(source: DecodedContractEvent): T
}
