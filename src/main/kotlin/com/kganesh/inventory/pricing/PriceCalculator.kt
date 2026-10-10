package com.kganesh.inventory.pricing

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Computes order totals in cents.
 */
@Component
class PriceCalculator {

    /**
     * Total for [quantity] units at [unitPriceCents], after a percentage discount.
     * The discounted total is rounded half-up to the nearest cent.
     */
    fun total(unitPriceCents: Long, quantity: Int, discountPercent: Int = 0): Long {
        require(quantity >= 0) { "quantity must not be negative" }
        val gross = BigDecimal.valueOf(unitPriceCents).multiply(BigDecimal.valueOf(quantity.toLong()))
        val factor = BigDecimal.valueOf((100 - discountPercent).toLong()).divide(BigDecimal.valueOf(100))
        return gross.multiply(factor).setScale(0, RoundingMode.HALF_UP).toLong()
    }
}
