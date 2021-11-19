package nft.freeport.processor.webhook.impl.strapi

import nft.freeport.processor.webhook.impl.strapi.dto.AuthRequest
import nft.freeport.processor.webhook.impl.strapi.dto.AuthResponse
import javax.ws.rs.*
import javax.ws.rs.core.Response

interface StrapiClient {
    @Path("/admin/login")
    @POST
    fun auth(rq: AuthRequest): AuthResponse

    @Path("/{entity}")
    @POST
    fun create(
        @PathParam("entity") entity: String,
        payload: Any,
        @HeaderParam("Authorization") authorization: String
    ): Response

    @Path("/{entity}")
    @PUT
    fun update(
        @PathParam("entity") entity: String,
        payload: Any,
        @HeaderParam("Authorization") authorization: String
    ): Response
}