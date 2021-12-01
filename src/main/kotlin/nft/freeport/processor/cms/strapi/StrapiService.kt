package nft.freeport.processor.cms.strapi

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import nft.freeport.processor.cms.CmsConfig
import nft.freeport.processor.cms.strapi.dto.AuthRequest
import nft.freeport.processor.cms.strapi.dto.AuthResponse
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StrapiService(config: CmsConfig, vertx: Vertx) {
    private val authRequest = AuthRequest(config.login(), config.password())
    private val routes = config.routes()
    private val baseUrl = config.baseUrl()
    private val client = WebClient.create(vertx)

    fun create(route: CmsConfig.Routes.() -> String, payload: Any) {
        client.postAbs("$baseUrl/${route(routes)}")
            .bearerTokenAuthentication(jwt())
            .sendJson(payload)
            .await()
    }

    fun findAll(route: CmsConfig.Routes.() -> String, filters: Map<String, Any>): JsonArray {
        val queryParams = filters.map { "${it.key}=${it.value}" }.joinToString("&")
        return client.getAbs("$baseUrl/${route(routes)}?$queryParams")
            .bearerTokenAuthentication(jwt())
            .send()
            .await()
            .bodyAsJsonArray()
    }

    fun findOne(route: CmsConfig.Routes.() -> String, filters: Map<String, Any>): JsonObject? {
        return findAll(route, filters)
            .firstOrNull()
            ?.let { it as JsonObject }
    }

    fun findId(route: CmsConfig.Routes.() -> String, filters: Map<String, Any>): Long? {
        return findOne(route, filters)?.getLong("id")
    }

    fun update(route: CmsConfig.Routes.() -> String, id: Long, payload: Any) {
        client.putAbs("$baseUrl/${route(routes)}/$id")
            .bearerTokenAuthentication(jwt())
            .sendJson(payload)
            .await()
    }

    private fun jwt(): String {
        return client.postAbs("$baseUrl/admin/login")
            .sendJson(authRequest)
            .await()
            .bodyAsJson(AuthResponse::class.java)
            .data.token
    }

    private fun Future<HttpResponse<Buffer>>.await() = toCompletionStage()
        .toCompletableFuture()
        .get()
}