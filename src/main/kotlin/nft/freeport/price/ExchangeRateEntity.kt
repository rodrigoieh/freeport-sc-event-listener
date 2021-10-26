package nft.freeport.price

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import java.math.RoundingMode
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "exchange_rate", schema = "api")
class ExchangeRateEntity(
    @Id
    @Column(name = "cere_units_per_penny")
    var cereUnitsPerPenny: BigInteger
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<ExchangeRateEntity, BigInteger>

    fun convert(priceInCereTokens: BigInteger) : BigInteger {
        return priceInCereTokens.toBigDecimal()
            .divide(cereUnitsPerPenny.toBigDecimal(), RoundingMode.CEILING)
            .toBigIntegerExact()
    }
}