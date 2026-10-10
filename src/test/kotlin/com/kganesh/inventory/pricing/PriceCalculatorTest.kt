package com.kganesh.inventory.pricing

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class PriceCalculatorTest {

    private val calc = PriceCalculator()

    @Test
    fun `total without discount is unit price times quantity`() {
        assertEquals(2997L, calc.total(999, 3))
    }

    @Test
    fun `discount is applied to the whole order and rounded half up`() {
        // 999 * 3 = 2997; 15% off = 2547.45 -> 2547
        assertEquals(2547L, calc.total(999, 3, 15))
        // 5 * 1 = 5; 10% off = 4.5 -> 5
        assertEquals(5L, calc.total(5, 1, 10))
    }

    @Test
    fun `zero quantity costs nothing`() {
        assertEquals(0L, calc.total(999, 0, 50))
    }

    @Test
    fun `negative quantity is rejected`() {
        assertThrows<IllegalArgumentException> { calc.total(999, -1) }
    }
}
