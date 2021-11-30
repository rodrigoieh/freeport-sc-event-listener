package nft.freeport.processor.cms.strapi

import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.strapi.dto.AuthRequest
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StrapiService(config: CmsConfig, @RestClient private val strapi: StrapiClient) {
    private val authRequest = AuthRequest(config.login(), config.password())
    private val routes = config.routes()

    fun create(route: CmsConfig.Routes.() -> String, payload: Any) {
        strapi.create(route.invoke(routes), payload, jwt())
    }

    fun updateSingle(route: CmsConfig.Routes.() -> String, payload: Any) {
        strapi.updateSingle(route.invoke(routes), payload, jwt())
    }

    private fun jwt() = strapi.auth(authRequest).data.token.let { "Bearer $it" }
}