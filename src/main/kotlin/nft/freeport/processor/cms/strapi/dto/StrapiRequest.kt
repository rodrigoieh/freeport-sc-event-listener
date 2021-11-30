package nft.freeport.processor.cms.strapi.dto

data class StrapiRequest<T : Any>(
    val data: T
)
