package nft.davinci.ddc

import io.vertx.mutiny.core.Vertx
import network.cere.ddc.client.producer.DdcProducer
import network.cere.ddc.client.producer.ProducerConfig
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class DdcProvider(private val ddcConfig: DdcConfig, private val vertx: Vertx) {
    @Produces
    fun ddcProducer(): DdcProducer {
        return DdcProducer(
            ProducerConfig(
                ddcConfig.pubKeyHex(),
                ddcConfig.secKeyHex(),
                listOf(ddcConfig.bootNode())
            ),
            vertx
        )
    }
}
