package nft.freeport.network.converter

import nft.freeport.event.SmartContractEvent
import nft.freeport.network.config.ContractConfig
import nft.freeport.network.config.ContractsConfig
import nft.freeport.network.dto.ContractEvent

interface ContractEventConverter<T : SmartContractEvent> {
    fun canConvert(source: ContractEvent): Boolean

    fun convert(source: ContractEvent): T

    fun eventTopic(contractsConfig: ContractsConfig, clazz: Class<T>) = contractsConfig.contracts().values
        .asSequence()
        .map(ContractConfig::eventTopics)
        .flatMap { it.entries }
        .first { it.key == clazz.simpleName }
        .value
}
