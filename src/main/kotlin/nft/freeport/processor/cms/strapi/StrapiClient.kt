package nft.freeport.processor.cms.strapi

import nft.freeport.processor.cms.strapi.dto.AuthRequest
import nft.freeport.processor.cms.strapi.dto.AuthResponse
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.Response

@RegisterRestClient(configKey = "strapi")
@ApplicationScoped
interface StrapiClient {
    @Path("/admin/login")
    @POST
    fun auth(rq: AuthRequest): AuthResponse

    @Path("/{entity}")
    @POST
    fun create(
        @PathParam("entity") entity: String,
        rq: Any,
        @HeaderParam("Authorization") authorization: String
    ): Response

    @Path("/{entity}/{id}")
    @PUT
    fun update(
        @PathParam("entity") entity: String,
        @PathParam("id") id: Long,
        rq: Any,
        @HeaderParam("Authorization") authorization: String
    ): Response
}