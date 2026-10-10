package com.kganesh.inventory.service;

import com.kganesh.inventory.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryServiceTest {

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService(new InMemoryProductRepository());
        service.create(new Product("SKU-1", "Widget", 999, 10));
    }

    @Test
    void getReturnsCreatedProduct() {
        assertThat(service.get("SKU-1").name()).isEqualTo("Widget");
    }

    @Test
    void getUnknownSkuThrows() {
        assertThatThrownBy(() -> service.get("NOPE"))
                .isInstanceOf(InventoryService.ProductNotFoundException.class);
    }

    @Test
    void duplicateSkuIsRejected() {
        assertThatThrownBy(() -> service.create(new Product("SKU-1", "Other", 1, 1)))
                .isInstanceOf(InventoryService.DuplicateSkuException.class);
    }

    @Test
    void listIsSortedBySku() {
        service.create(new Product("SKU-0", "Gadget", 500, 1));
        assertThat(service.list()).extracting(Product::sku).containsExactly("SKU-0", "SKU-1");
    }

    @Test
    void adjustStockAddsAndRemoves() {
        assertThat(service.adjustStock("SKU-1", 5).stock()).isEqualTo(15);
        assertThat(service.adjustStock("SKU-1", -15).stock()).isZero();
    }

    @Test
    void adjustStockBelowZeroIsRejectedAndLeavesStockUnchanged() {
        assertThatThrownBy(() -> service.adjustStock("SKU-1", -11))
                .isInstanceOf(InventoryService.InsufficientStockException.class);
        assertThat(service.get("SKU-1").stock()).isEqualTo(10);
    }

    @Test
    void negativePriceIsRejectedByTheModel() {
        assertThatThrownBy(() -> new Product("SKU-2", "Bad", -1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
