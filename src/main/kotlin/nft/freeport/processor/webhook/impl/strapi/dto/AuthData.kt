package nft.freeport.processor.webhook.impl.strapi.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class AuthData(val token: String)
