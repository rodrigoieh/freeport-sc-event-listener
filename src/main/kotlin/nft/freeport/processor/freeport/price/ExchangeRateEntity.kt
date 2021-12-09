package nft.freeport.processor.freeport.price

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import java.math.BigInteger
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "exchange_rate")
class ExchangeRateEntity(
    @Id
    @Column(name = "cere_units_per_penny")
    var cereUnitsPerPenny: BigInteger
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<ExchangeRateEntity, BigInteger>
}