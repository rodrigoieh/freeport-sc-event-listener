package nft.freeport.listener.event

import nft.freeport.covalent.dto.ContractEvent

data class SmartContractEventData<T : SmartContractEvent>(
    val contract: String,
    val event: T,
    val rawEvent: ContractEvent,
)