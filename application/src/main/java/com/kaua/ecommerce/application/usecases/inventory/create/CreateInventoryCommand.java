package com.kaua.ecommerce.application.usecases.inventory.create;

public record CreateInventoryCommand(
        String productId,
        String sku,
        int quantity
) {

    public static CreateInventoryCommand with(final String productId, final String sku, final int quantity) {
        return new CreateInventoryCommand(productId, sku, quantity);
    }
}
