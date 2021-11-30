package nft.freeport.processor.cms.strapi

import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.strapi.dto.AuthRequest
import nft.freeport.processor.cms.strapi.dto.StrapiRequest
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StrapiService(config: CmsConfig, @RestClient private val strapi: StrapiClient) {
    private val authRequest = AuthRequest(config.login(), config.password())
    private val routes = config.routes()

    fun <T : Any> create(route: CmsConfig.Routes.() -> String, payload: T) {
        strapi.create(route.invoke(routes), StrapiRequest(payload), jwt())
    }

    private fun jwt() = strapi.auth(authRequest).data.token
}