package nft.freeport.network

import nft.freeport.network.dto.Block
import nft.freeport.network.dto.ContractEvent
import nft.freeport.network.dto.CovalentResponse
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam

@RegisterRestClient(baseUri = "https://api.covalenthq.com")
@Path("/v1")
@ApplicationScoped
interface CovalentClient {
    @GET
    @Path("/{chainId}/block_v2/latest/")
    fun getLatestBlock(
        @PathParam("chainId") chainId: Int,
        @QueryParam("key") apiKey: String
    ): CovalentResponse<Block>

    @GET
    @Path("/{chainId}/events/address/{address}/")
    fun getContractEvents(
        @PathParam("chainId") chainId: Int,
        @PathParam("address") contractAddress: String,
        @QueryParam("starting-block") startingBlock: Long,
        @QueryParam("ending-block") endingBlock: Long,
        @QueryParam("key") apiKey: String
    ): CovalentResponse<ContractEvent>
}