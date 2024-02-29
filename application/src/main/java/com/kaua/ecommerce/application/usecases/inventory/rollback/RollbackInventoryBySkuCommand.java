package com.kaua.ecommerce.application.usecases.inventory.rollback;

public record RollbackInventoryBySkuCommand(
        String sku,
        String productId
) {

    public static RollbackInventoryBySkuCommand with(final String sku, final String productId) {
        return new RollbackInventoryBySkuCommand(sku, productId);
    }
}
