package nft.freeport.processor.ddc

import io.quarkus.arc.DefaultBean
import io.quarkus.arc.properties.IfBuildProperty
import io.vertx.mutiny.core.Vertx
import network.cere.ddc.client.producer.DdcProducer
import network.cere.ddc.client.producer.Producer
import network.cere.ddc.client.producer.ProducerConfig
import javax.enterprise.inject.Produces

class DdcProvider {
    @Produces
    @IfBuildProperty(name = "ddc.enabled", stringValue = "true")
    fun ddcProducer(ddcConfig: DdcConfig, vertx: Vertx): Producer {
        return DdcProducer(
            config = ProducerConfig(
                appPubKey = ddcConfig.pubKeyHex(),
                appPrivKey = ddcConfig.secKeyHex(),
                bootstrapNodes = ddcConfig.bootNodes()
            ),
            vertx = vertx
        )
    }

    @Produces
    @DefaultBean
    fun noOpDdcProducer(): Producer = NoOpDdcProducer()
}
