package com.kaua.ecommerce.application.usecases.inventory.retrieve.get;

import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.inventory.Inventory;

import java.util.Objects;

public class DefaultGetInventoryBySkuUseCase extends GetInventoryBySkuUseCase {

    private final InventoryGateway inventoryGateway;

    public DefaultGetInventoryBySkuUseCase(final InventoryGateway inventoryGateway) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
    }

    @Override
    public GetInventoryBySkuOutput execute(String aSku) {
        return this.inventoryGateway.findBySku(aSku)
                .map(GetInventoryBySkuOutput::from)
                .orElseThrow(NotFoundException.with(Inventory.class, aSku));
    }
}
