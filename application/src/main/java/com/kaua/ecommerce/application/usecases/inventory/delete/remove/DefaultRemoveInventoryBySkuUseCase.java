package com.kaua.ecommerce.application.usecases.inventory.delete.remove;

import com.kaua.ecommerce.application.gateways.InventoryGateway;

import java.util.Objects;

public class DefaultRemoveInventoryBySkuUseCase extends RemoveInventoryBySkuUseCase {

    private final InventoryGateway inventoryGateway;

    public DefaultRemoveInventoryBySkuUseCase(final InventoryGateway inventoryGateway) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
    }

    @Override
    public void execute(String sku) {
        this.inventoryGateway.deleteBySku(sku);
    }
}
