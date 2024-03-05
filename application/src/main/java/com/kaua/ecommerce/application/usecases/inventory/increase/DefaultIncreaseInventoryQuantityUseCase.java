package com.kaua.ecommerce.application.usecases.inventory.increase;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultIncreaseInventoryQuantityUseCase extends IncreaseInventoryQuantityUseCase {

    private final InventoryGateway inventoryGateway;
    private final InventoryMovementGateway inventoryMovementGateway;
    private final TransactionManager transactionManager;

    public DefaultIncreaseInventoryQuantityUseCase(
            final InventoryGateway inventoryGateway,
            final InventoryMovementGateway inventoryMovementGateway,
            final TransactionManager transactionManager
    ) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
        this.inventoryMovementGateway = Objects.requireNonNull(inventoryMovementGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public Either<NotificationHandler, IncreaseInventoryQuantityOutput> execute(IncreaseInventoryQuantityCommand input) {
        final var aNotification = NotificationHandler.create();

        if (input.quantity() <= 0) {
            return Either.left(aNotification.append(
                    new Error(CommonErrorMessage.greaterThan("quantity", 0))));
        }

        final var aInventory = this.inventoryGateway.findBySku(input.sku())
                .orElseThrow(NotFoundException.with(Inventory.class, input.sku()));

        final var aInventoryIncreased = aInventory.increaseQuantity(input.quantity());

        final var aInventoryMovement = InventoryMovement.newInventoryMovement(
                aInventoryIncreased.getId(),
                aInventoryIncreased.getSku(),
                input.quantity(),
                InventoryMovementStatus.IN
        );

        aInventoryIncreased.validate(aNotification);
        aInventoryMovement.validate(aNotification);

        final var aTransactionResult = this.transactionManager.execute(() -> {
            final var aResult = this.inventoryGateway.update(aInventoryIncreased);
            this.inventoryMovementGateway.create(aInventoryMovement);
            return aResult;
        });

        if (aTransactionResult.isFailure()) {
            throw TransactionFailureException.with(aTransactionResult.getErrorResult());
        }

        return Either.right(IncreaseInventoryQuantityOutput.from(aInventoryIncreased));
    }
}
