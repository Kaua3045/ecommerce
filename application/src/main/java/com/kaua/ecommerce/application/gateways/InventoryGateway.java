package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.inventory.Inventory;

public interface InventoryGateway {

    Inventory create(Inventory inventory);

    boolean existsBySku(String sku);
}
