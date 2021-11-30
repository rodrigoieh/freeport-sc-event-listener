package nft.freeport.processor.cms.strapi.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import nft.freeport.processor.cms.strapi.dto.AuthData

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthResponse(val data: AuthData)
