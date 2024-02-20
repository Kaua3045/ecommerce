package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultCreateInventoryUseCase extends CreateInventoryUseCase {

    private final InventoryGateway inventoryGateway;

    public DefaultCreateInventoryUseCase(final InventoryGateway inventoryGateway) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
    }

    @Override
    public Either<NotificationHandler, CreateInventoryOutput> execute(CreateInventoryCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.inventoryGateway.existsBySku(input.sku())) {
            return Either.left(aNotification.append(new Error("'sku' already exists")));
        }

        final var aInventory = Inventory.newInventory(input.productId(), input.sku(), input.quantity());
        aInventory.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        return Either.right(CreateInventoryOutput.from(this.inventoryGateway.create(aInventory)));
    }
}
