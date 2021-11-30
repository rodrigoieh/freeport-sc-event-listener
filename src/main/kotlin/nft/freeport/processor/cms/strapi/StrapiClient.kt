package nft.freeport.processor.cms.strapi

import nft.freeport.processor.cms.strapi.dto.AuthRequest
import nft.freeport.processor.cms.strapi.dto.AuthResponse
import nft.freeport.processor.cms.strapi.dto.StrapiRequest
import nft.freeport.processor.cms.strapi.dto.StrapiResponse
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
    @GET
    fun <T : Any> get(
        @PathParam("entity") entity: String,
        @MatrixParam("filters") filters: String,
        @HeaderParam("Authorization") authorization: String
    ): StrapiResponse<T>

    @Path("/{entity}")
    @POST
    fun <T : Any> create(
        @PathParam("entity") entity: String,
        payload: StrapiRequest<T>,
        @HeaderParam("Authorization") authorization: String
    ): Response

    @Path("/{entity}/{id}")
    @PUT
    fun <T : Any> update(
        @PathParam("entity") entity: String,
        @PathParam("id") id: Long,
        payload: StrapiRequest<T>,
        @HeaderParam("Authorization") authorization: String
    ): Response
}