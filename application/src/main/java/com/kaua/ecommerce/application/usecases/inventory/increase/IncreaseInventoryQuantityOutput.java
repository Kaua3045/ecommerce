package com.kaua.ecommerce.application.usecases.inventory.increase;

import com.kaua.ecommerce.domain.inventory.Inventory;

public record IncreaseInventoryQuantityOutput(
        String inventoryId,
        String productId,
        String sku
) {

    public static IncreaseInventoryQuantityOutput from(final Inventory aInventory) {
        return new IncreaseInventoryQuantityOutput(
                aInventory.getId().getValue(),
                aInventory.getProductId(),
                aInventory.getSku()
        );
    }
}
