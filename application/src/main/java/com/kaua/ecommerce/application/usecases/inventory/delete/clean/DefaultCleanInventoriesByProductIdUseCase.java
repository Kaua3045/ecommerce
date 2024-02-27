package com.kaua.ecommerce.application.usecases.inventory.delete.clean;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;

import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCleanInventoriesByProductIdUseCase extends CleanInventoriesByProductIdUseCase {

    private final InventoryGateway inventoryGateway;
    private final InventoryMovementGateway inventoryMovementGateway;
    private final TransactionManager transactionManager;

    public DefaultCleanInventoriesByProductIdUseCase(
            final InventoryGateway inventoryGateway,
            final InventoryMovementGateway inventoryMovementGateway,
            final TransactionManager transactionManager
    ) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
        this.inventoryMovementGateway = Objects.requireNonNull(inventoryMovementGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public void execute(String aProductId) {
        final var aInventories = this.inventoryGateway.findByProductId(aProductId);

        if (!aInventories.isEmpty()) {
            final var aTransactionResult = this.transactionManager.execute(() -> {
                final var aInventoriesMovement = aInventories.stream()
                        .map(inventory -> InventoryMovement.newInventoryMovement(
                                inventory.getId(),
                                inventory.getSku(),
                                inventory.getQuantity(),
                                InventoryMovementStatus.REMOVED
                        )).collect(Collectors.toSet());

                this.inventoryGateway.cleanByProductId(aProductId);
                this.inventoryMovementGateway.createInBatch(aInventoriesMovement);
                return null;
            });

            if (aTransactionResult.isFailure()) {
                throw TransactionFailureException.with(aTransactionResult.getErrorResult());
            }
        }
    }
}
