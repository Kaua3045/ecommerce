package com.kaua.ecommerce.application.usecases.inventory.increase;

public record IncreaseInventoryQuantityCommand(
        String sku,
        int quantity
) {

    public static IncreaseInventoryQuantityCommand with(final String sku, final int quantity) {
        return new IncreaseInventoryQuantityCommand(sku, quantity);
    }
}
