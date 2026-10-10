package com.kganesh.inventory.service;

import com.kganesh.inventory.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final ProductRepository repository;

    public InventoryService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> list() {
        return repository.findAll();
    }

    public Product get(String sku) {
        return repository.findBySku(sku).orElseThrow(() -> new ProductNotFoundException(sku));
    }

    public Product create(Product product) {
        if (!repository.insert(product)) {
            throw new DuplicateSkuException(product.sku());
        }
        return product;
    }

    /**
     * Adds delta (positive or negative) to the stock level.
     */
    public synchronized Product adjustStock(String sku, int delta) {
        Product current = get(sku);
        int next = current.stock() + delta;
        if (next < 0) {
            throw new InsufficientStockException(sku, current.stock(), -delta);
        }
        Product updated = current.withStock(next);
        repository.update(updated);
        return updated;
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String sku) {
            super("product not found: " + sku);
        }
    }

    public static class DuplicateSkuException extends RuntimeException {
        public DuplicateSkuException(String sku) {
            super("sku already exists: " + sku);
        }
    }

    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String sku, int available, int requested) {
            super("insufficient stock for " + sku + ": available " + available + ", requested " + requested);
        }
    }
}
