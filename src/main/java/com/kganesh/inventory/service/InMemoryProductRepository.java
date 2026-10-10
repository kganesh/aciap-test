package com.kganesh.inventory.service;

import com.kganesh.inventory.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final ConcurrentMap<String, Product> products = new ConcurrentHashMap<>();

    @Override
    public Optional<Product> findBySku(String sku) {
        return Optional.ofNullable(products.get(sku));
    }

    @Override
    public List<Product> findAll() {
        return products.values().stream()
                .sorted(Comparator.comparing(Product::sku))
                .toList();
    }

    @Override
    public boolean insert(Product product) {
        return products.putIfAbsent(product.sku(), product) == null;
    }

    @Override
    public void update(Product product) {
        products.put(product.sku(), product);
    }
}
