package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.inventory.Inventory;

import java.util.List;
import java.util.Set;

public interface InventoryGateway {

    Set<Inventory> createInBatch(Set<Inventory> inventories);

    List<String> existsBySkus(List<String> skus);

    void cleanByProductId(String productId);

    void deleteBySku(String sku);
}
