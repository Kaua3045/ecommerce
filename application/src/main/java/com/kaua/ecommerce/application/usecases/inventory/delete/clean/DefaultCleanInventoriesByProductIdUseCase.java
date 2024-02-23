package com.kaua.ecommerce.application.usecases.inventory.delete.clean;

import com.kaua.ecommerce.application.gateways.InventoryGateway;

import java.util.Objects;

public class DefaultCleanInventoriesByProductIdUseCase extends CleanInventoriesByProductIdUseCase {

    private final InventoryGateway inventoryGateway;

    public DefaultCleanInventoriesByProductIdUseCase(final InventoryGateway inventoryGateway) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
    }

    @Override
    public void execute(String aProductId) {
        this.inventoryGateway.cleanByProductId(aProductId);
    }
}
