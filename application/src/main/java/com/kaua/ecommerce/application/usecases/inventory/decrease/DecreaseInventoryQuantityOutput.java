package com.kaua.ecommerce.application.usecases.inventory.decrease;

import com.kaua.ecommerce.domain.inventory.Inventory;

public record DecreaseInventoryQuantityOutput(
        String inventoryId,
        String productId,
        String sku
) {

    public static DecreaseInventoryQuantityOutput from(final Inventory aInventory) {
        return new DecreaseInventoryQuantityOutput(
                aInventory.getId().getValue(),
                aInventory.getProductId(),
                aInventory.getSku()
        );
    }
}
