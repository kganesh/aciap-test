package com.kganesh.inventory.model;

import java.util.Objects;

/**
 * A stocked product. Prices are in cents to avoid floating-point money.
 */
public record Product(String sku, String name, long priceCents, int stock) {

    public Product {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        if (sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("priceCents must not be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("stock must not be negative");
        }
    }

    public Product withStock(int newStock) {
        return new Product(sku, name, priceCents, newStock);
    }
}
