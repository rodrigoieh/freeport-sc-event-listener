package nft.freeport;

import io.quarkus.test.Mock
import io.smallrye.config.SmallRyeConfig
import nft.freeport.listener.config.ContractsConfig
import org.eclipse.microprofile.config.Config
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces
import javax.inject.Inject

class ContractsConfigMockProducer {

    @Inject
    lateinit var config: Config

    @Mock
    @Produces
    @ApplicationScoped
    fun mockedContractsConfigs(): ContractsConfig =
        config.unwrap(SmallRyeConfig::class.java).getConfigMapping(ContractsConfig::class.java)

}