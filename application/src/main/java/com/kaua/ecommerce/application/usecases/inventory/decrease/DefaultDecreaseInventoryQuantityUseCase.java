package com.kaua.ecommerce.application.usecases.inventory.decrease;

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

public class DefaultDecreaseInventoryQuantityUseCase extends DecreaseInventoryQuantityUseCase {

    private final InventoryGateway inventoryGateway;
    private final InventoryMovementGateway inventoryMovementGateway;
    private final TransactionManager transactionManager;

    public DefaultDecreaseInventoryQuantityUseCase(
            final InventoryGateway inventoryGateway,
            final InventoryMovementGateway inventoryMovementGateway,
            final TransactionManager transactionManager
    ) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
        this.inventoryMovementGateway = Objects.requireNonNull(inventoryMovementGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public Either<NotificationHandler, DecreaseInventoryQuantityOutput> execute(DecreaseInventoryQuantityCommand input) {
        final var aNotification = NotificationHandler.create();

        if (input.quantity() <= 0) {
            return Either.left(aNotification.append(
                    new Error(CommonErrorMessage.greaterThan("quantity", 0))));
        }

        final var aInventory = this.inventoryGateway.findBySku(input.sku())
                .orElseThrow(NotFoundException.with(Inventory.class, input.sku()));

        final var aInventoryDecreased = aInventory.decreaseQuantity(input.quantity());

        final var aInventoryMovement = InventoryMovement.newInventoryMovement(
                aInventoryDecreased.getId(),
                aInventoryDecreased.getSku(),
                input.quantity(),
                InventoryMovementStatus.OUT
        );

        aInventoryDecreased.validate(aNotification);
        aInventoryMovement.validate(aNotification);

        final var aTransactionResult = this.transactionManager.execute(() -> {
            final var aResult = this.inventoryGateway.update(aInventoryDecreased);
            this.inventoryMovementGateway.create(aInventoryMovement);
            return aResult;
        });

        if (aTransactionResult.isFailure()) {
            throw TransactionFailureException.with(aTransactionResult.getErrorResult());
        }

        return Either.right(DecreaseInventoryQuantityOutput.from(aInventoryDecreased));
    }
}
