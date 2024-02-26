package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommandParams;
import com.kaua.ecommerce.application.usecases.inventory.create.outputs.CreateInventoryOutput;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.utils.CollectionUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.AbstractMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultCreateInventoryUseCase extends CreateInventoryUseCase {

    private final InventoryGateway inventoryGateway;
    private final InventoryMovementGateway inventoryMovementGateway;
    private final TransactionManager transactionManager;

    public DefaultCreateInventoryUseCase(
            final InventoryGateway inventoryGateway,
            final InventoryMovementGateway inventoryMovementGateway,
            final TransactionManager transactionManager
    ) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
        this.inventoryMovementGateway = Objects.requireNonNull(inventoryMovementGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
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

                    final var aInventoryMovement = InventoryMovement
                            .newInventoryMovement(
                                    aInventory.getId(),
                                    aInventory.getSku(),
                                    aInventory.getQuantity(),
                                    InventoryMovementStatus.IN
                            );
                    aInventoryMovement.validate(aNotification);

                    return new AbstractMap.SimpleImmutableEntry<>(aInventory, aInventoryMovement);
                })
                .collect(Collectors.toSet());

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        final var aTransactionResult = this.transactionManager.execute(() -> {
            final var aInventoriesCreateResult = this.inventoryGateway
                    .createInBatch(CollectionUtils
                            .mapTo(aInventories, AbstractMap.SimpleImmutableEntry::getKey));
            this.inventoryMovementGateway
                    .createInBatch(CollectionUtils
                            .mapTo(aInventories, AbstractMap.SimpleImmutableEntry::getValue));
            return aInventoriesCreateResult;
        });

        if (aTransactionResult.isFailure()) {
            throw TransactionFailureException.with(aTransactionResult.getErrorResult());
        }

        return Either.right(CreateInventoryOutput.from(input.productId(), aTransactionResult.getSuccessResult()));
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
