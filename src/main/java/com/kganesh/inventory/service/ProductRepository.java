package com.kganesh.inventory.service;

import com.kganesh.inventory.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Optional<Product> findBySku(String sku);

    List<Product> findAll();

    /** Inserts the product; returns false if the SKU already exists. */
    boolean insert(Product product);

    void update(Product product);
}
