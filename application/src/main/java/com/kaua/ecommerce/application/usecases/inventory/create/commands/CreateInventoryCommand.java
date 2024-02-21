package com.kaua.ecommerce.application.usecases.inventory.create;

import java.util.Set;

public record CreateInventoryCommand(
        String productId,
        Set<CreateInventoryCommandParams> inventoryParams
) {

    public static CreateInventoryCommand with(
            final String productId,
            final Set<CreateInventoryCommandParams> inventoryParams
    ) {
        return new CreateInventoryCommand(productId, inventoryParams);
    }
}
