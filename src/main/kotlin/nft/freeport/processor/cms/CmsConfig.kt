package nft.freeport.processor.cms

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "cms")
interface CmsConfig {
    fun enabled(): Boolean
    fun baseUrl(): String
    fun login(): String
    fun password(): String
    fun routes(): Routes

    interface Routes {
        fun nft(): String
        fun nftCid(): String
        fun makeOffer(): String
        fun takeOffer(): String
        fun auction(): String
        fun auctionBid(): String
        fun wallet(): String
        fun exchangeRate(): String
    }
}