package nft.freeport.processor.webhook.impl.strapi.dto

data class AuthRequest(
    val email: String,
    val password: String
)
