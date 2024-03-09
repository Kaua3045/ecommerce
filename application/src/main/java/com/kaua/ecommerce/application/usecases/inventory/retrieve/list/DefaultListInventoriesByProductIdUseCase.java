package com.kaua.ecommerce.application.usecases.inventory.retrieve.list;

import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.pagination.Pagination;

import java.util.Objects;

public class DefaultListInventoriesByProductIdUseCase extends ListInventoriesByProductIdUseCase {

    private final InventoryGateway inventoryGateway;

    public DefaultListInventoriesByProductIdUseCase(final InventoryGateway inventoryGateway) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
    }

    @Override
    public Pagination<ListInventoriesByProductIdOutput> execute(final ListInventoriesByProductIdCommand input) {
        return this.inventoryGateway.findAllByProductId(input.searchQuery(), input.productId())
                .map(ListInventoriesByProductIdOutput::from);
    }
}
