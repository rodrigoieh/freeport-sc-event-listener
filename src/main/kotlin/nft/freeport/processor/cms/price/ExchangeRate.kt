package nft.freeport.processor.cms.price

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class ExchangeRate(
    @field:JsonProperty("cere_units_per_penny")
    val cereUnitsPerPenny: BigInteger
)
