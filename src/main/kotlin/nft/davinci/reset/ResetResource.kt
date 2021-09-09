package nft.davinci.reset

import nft.davinci.network.ContractEventsListener
import nft.davinci.network.ContractEventsListenerLifecycle
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("reset")
class ResetResource(private val contractEventsListenerLifecycle: ContractEventsListenerLifecycle) {
    @GET
    suspend fun reset() {
        contractEventsListenerLifecycle.reset()
    }
}
