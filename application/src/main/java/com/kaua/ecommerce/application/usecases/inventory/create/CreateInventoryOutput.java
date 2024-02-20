package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.domain.inventory.Inventory;

public record CreateInventoryOutput(
        String inventoryId,
        String productId,
        String sku
) {

    public static CreateInventoryOutput from(final Inventory aInventory) {
        return new CreateInventoryOutput(
                aInventory.getId().getValue(),
                aInventory.getProductId(),
                aInventory.getSku()
        );
    }
}
