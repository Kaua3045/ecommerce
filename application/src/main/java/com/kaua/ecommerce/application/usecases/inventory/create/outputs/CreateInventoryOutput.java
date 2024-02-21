package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.domain.inventory.Inventory;

import java.util.Set;
import java.util.stream.Collectors;

public record CreateInventoryOutput(
        String productId,
        Set<CreateInventoryOutputParams> inventories
) {

    public static CreateInventoryOutput from(final String productId, final Set<Inventory> inventories) {
        return new CreateInventoryOutput(
                productId,
                inventories.stream()
                        .map(CreateInventoryOutputParams::from)
                        .collect(Collectors.toSet())
        );
    }
}
