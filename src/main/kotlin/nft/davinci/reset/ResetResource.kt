package nft.davinci.reset

import nft.davinci.network.ContractEventsListener
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("reset")
class ResetResource(private val contractEventsListener: ContractEventsListener) {
    @GET
    suspend fun reset() {
        contractEventsListener.reset()
    }
}
