package com.kganesh.inventory.web

import com.kganesh.inventory.model.Product
import com.kganesh.inventory.pricing.PriceCalculator
import com.kganesh.inventory.service.InventoryService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

data class CreateProductRequest(
    @field:NotBlank val sku: String,
    @field:NotBlank val name: String,
    @field:Min(0) val priceCents: Long,
    @field:Min(0) val stock: Int = 0,
)

data class StockAdjustment(val delta: Int)

data class QuoteResponse(val sku: String, val quantity: Int, val discountPercent: Int, val totalCents: Long)

@RestController
@RequestMapping("/products")
class ProductController(
    private val inventory: InventoryService,
    private val pricing: PriceCalculator,
) {

    @GetMapping
    fun list(): List<Product> = inventory.list()

    @GetMapping("/{sku}")
    fun get(@PathVariable sku: String): Product = inventory.get(sku)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateProductRequest): Product =
        inventory.create(Product(req.sku, req.name, req.priceCents, req.stock))

    @PostMapping("/{sku}/stock")
    fun adjustStock(@PathVariable sku: String, @RequestBody adj: StockAdjustment): Product =
        inventory.adjustStock(sku, adj.delta)

    @GetMapping("/{sku}/quote")
    fun quote(
        @PathVariable sku: String,
        @RequestParam quantity: Int,
        @RequestParam(defaultValue = "0") discountPercent: Int,
    ): QuoteResponse {
        val product = inventory.get(sku)
        val total = pricing.total(product.priceCents(), quantity, discountPercent)
        return QuoteResponse(sku, quantity, discountPercent, total)
    }
}
