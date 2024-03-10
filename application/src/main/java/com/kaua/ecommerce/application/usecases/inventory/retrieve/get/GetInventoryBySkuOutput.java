package com.kaua.ecommerce.application.usecases.inventory.retrieve.get;

import com.kaua.ecommerce.domain.inventory.Inventory;

import java.time.Instant;

public record GetInventoryBySkuOutput(
        String id,
        String productId,
        String sku,
        int quantity,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    public static GetInventoryBySkuOutput from(final Inventory aInventory) {
        return new GetInventoryBySkuOutput(
                aInventory.getId().getValue(),
                aInventory.getProductId(),
                aInventory.getSku(),
                aInventory.getQuantity(),
                aInventory.getCreatedAt(),
                aInventory.getUpdatedAt(),
                aInventory.getVersion()
        );
    }
}
