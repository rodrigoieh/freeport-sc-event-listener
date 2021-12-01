package nft.freeport.processor.cms.strapi.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class StrapiResponse<T : Any>(
    val data: T
)
