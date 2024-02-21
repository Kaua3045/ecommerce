package com.kaua.ecommerce.application.usecases.inventory.create.commands;

public record CreateInventoryCommandParams(
        String sku,
        int quantity
) {

    public static CreateInventoryCommandParams with(final String sku, final int quantity) {
        return new CreateInventoryCommandParams(sku, quantity);
    }
}
