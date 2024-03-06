package com.kaua.ecommerce.application.usecases.inventory.decrease;

public record DecreaseInventoryQuantityCommand(
        String sku,
        int quantity
) {

    public static DecreaseInventoryQuantityCommand with(final String sku, final int quantity) {
        return new DecreaseInventoryQuantityCommand(sku, quantity);
    }
}
