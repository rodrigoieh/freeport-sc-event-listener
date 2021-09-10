package nft.davinci.network.converter

import nft.davinci.event.SmartContractEvent
import nft.davinci.network.config.ContractConfig
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.dto.ContractEvent

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
