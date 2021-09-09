package nft.davinci.network

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import kotlinx.coroutines.launch
import nft.davinci.event.SmartContractEvent
import nft.davinci.network.config.ContractsConfig
import nft.davinci.network.config.NetworkConfig
import nft.davinci.network.converter.ContractEventConverter
import nft.davinci.network.processor.EventProcessor
import nft.davinci.reset.CleanUp
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.server.runtime.kotlin.ApplicationCoroutineScope
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.Instance

@ApplicationScoped
class ContractEventsListenerLifecycle(
    contractsConfig: ContractsConfig,
    private val applicationCoroutineScope: ApplicationCoroutineScope,
    private val networkConfig: NetworkConfig,
    private val lastScannedBlockRepository: LastScannedBlockRepository,
    @RestClient private val covalentClient: CovalentClient,
    private val converters: Instance<ContractEventConverter<*>>,
    private val processorsMap: Map<String, EventProcessor<SmartContractEvent>>,
    private val cleanUp: CleanUp
) {
    private val listeners = contractsConfig.contracts().values.map {
        ContractEventsListener(
            it,
            networkConfig,
            lastScannedBlockRepository,
            covalentClient,
            converters,
            processorsMap
        )
    }

    fun onStart(@Observes e: StartupEvent) {
        init()
    }

    private fun init() {
        listeners.forEach { l -> applicationCoroutineScope.launch { l.init() } }
    }

    suspend fun reset() {
        listeners.forEach { it.reset() }
        cleanUp.truncateDb()
        init()
    }

    fun onStop(@Observes ev: ShutdownEvent) {
        listeners.forEach { it.stop() }
    }
}