package nft.freeport.processor.webhook.impl.strapi.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthResponse(val data: AuthData)
