package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommandParams;
import com.kaua.ecommerce.application.usecases.inventory.create.outputs.CreateInventoryOutput;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultCreateInventoryUseCase extends CreateInventoryUseCase {

    private final InventoryGateway inventoryGateway;

    public DefaultCreateInventoryUseCase(final InventoryGateway inventoryGateway) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
    }

    @Override
    public Either<NotificationHandler, CreateInventoryOutput> execute(CreateInventoryCommand input) {
        final var aNotification = NotificationHandler.create();

        final var aInventoryParams = this.returnWithSkusValid(input.inventoryParams());

        final var aInventories = aInventoryParams.stream()
                .map(aParam -> {
                    final var aInventory = Inventory.newInventory(
                            input.productId(),
                            aParam.sku(),
                            aParam.quantity());
                    aInventory.validate(aNotification);

                    return aInventory;
                })
                .collect(Collectors.toSet());

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        final var aInventoriesCreateResult = this.inventoryGateway.createInBatch(aInventories);

        return Either.right(CreateInventoryOutput.from(input.productId(), aInventoriesCreateResult));
    }

    private Set<CreateInventoryCommandParams> returnWithSkusValid(final Set<CreateInventoryCommandParams> input) {
        if (input == null || input.isEmpty()) {
            throw DomainException.with(new Error("Inventory params cannot be null or empty"));
        }

        final var aExistsSkus = this.inventoryGateway.existsBySkus(input.stream()
                .map(CreateInventoryCommandParams::sku)
                .toList());

        if (input.size() == aExistsSkus.size()) {
            throw DomainException.with(new Error("All skus already exists"));
        }

        input.removeIf(aParams -> aExistsSkus.contains(aParams.sku()));

        return input;
    }
}
