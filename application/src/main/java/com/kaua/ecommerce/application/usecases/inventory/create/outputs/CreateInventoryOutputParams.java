package com.kaua.ecommerce.application.usecases.inventory.create.outputs;

import com.kaua.ecommerce.domain.inventory.Inventory;

public record CreateInventoryOutputParams(
        String inventoryId,
        String sku
) {

    public static CreateInventoryOutputParams from(final Inventory aInventory) {
        return new CreateInventoryOutputParams(
                aInventory.getId().getValue(),
                aInventory.getSku()
        );
    }
}
