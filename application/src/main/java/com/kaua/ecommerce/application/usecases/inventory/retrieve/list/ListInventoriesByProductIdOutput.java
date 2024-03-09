package com.kaua.ecommerce.application.usecases.inventory.retrieve.list;

import com.kaua.ecommerce.domain.inventory.Inventory;

import java.time.Instant;

public record ListInventoriesByProductIdOutput(
        String id,
        String productId,
        String sku,
        int quantity,
        Instant createdAt,
        Instant updatedAt
) {

    public static ListInventoriesByProductIdOutput from(final Inventory aInventory) {
        return new ListInventoriesByProductIdOutput(
                aInventory.getId().getValue(),
                aInventory.getProductId(),
                aInventory.getSku(),
                aInventory.getQuantity(),
                aInventory.getCreatedAt(),
                aInventory.getUpdatedAt()
        );
    }
}
