package nft.freeport.network

import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP
import nft.freeport.network.config.ContractConfig
import nft.freeport.network.config.ContractsConfig
import org.eclipse.microprofile.context.ManagedExecutor
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.transaction.Transactional

/**
 * It's need to move triggering and initialization logic from [ContractsEventsListener] to write stable tests.
 */
@ApplicationScoped
class ContractsEventListenerScheduler(
    private val contractsEventsListener: ContractsEventsListener,
    private val executor: ManagedExecutor,

    contractsConfig: ContractsConfig,
) {
    private val contracts = contractsConfig.contracts().values

    @Transactional
    fun onStart(@Observes e: StartupEvent) {
        contracts.forEach(contractsEventsListener::init)
    }

    @Scheduled(every = "{network.poll-interval}", skipExecutionIf = TestModeEnabled::class, concurrentExecution = SKIP)
    fun sync() {
        contracts.map(ContractConfig::address)
            .forEach { contract -> executor.execute { contractsEventsListener.sync(contract) } }
    }
}