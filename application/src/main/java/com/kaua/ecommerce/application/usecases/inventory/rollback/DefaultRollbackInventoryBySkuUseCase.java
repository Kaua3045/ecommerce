package com.kaua.ecommerce.application.usecases.inventory.rollback;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;

import java.util.Objects;
import java.util.Set;

public class DefaultRollbackInventoryBySkuUseCase extends RollbackInventoryBySkuUseCase {

    private final InventoryGateway inventoryGateway;
    private final InventoryMovementGateway inventoryMovementGateway;
    private final TransactionManager transactionManager;

    public DefaultRollbackInventoryBySkuUseCase(
            final InventoryGateway inventoryGateway,
            final InventoryMovementGateway inventoryMovementGateway,
            final TransactionManager transactionManager
    ) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
        this.inventoryMovementGateway = Objects.requireNonNull(inventoryMovementGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public void execute(RollbackInventoryBySkuCommand input) {
        final var aInventoryMovement = this.inventoryMovementGateway.findBySkuAndCreatedAtDescAndStatusRemoved(input.sku());

        if (aInventoryMovement.isPresent()) {
            final var aTransactionResult = this.transactionManager.execute(() -> {
                final var aInventory = Inventory.newInventory(
                        input.productId(),
                        aInventoryMovement.get().getSku(),
                        aInventoryMovement.get().getQuantity()
                );

                this.inventoryGateway.createInBatch(Set.of(aInventory));
                this.inventoryMovementGateway.create(InventoryMovement.newInventoryMovement(
                        aInventory.getId(),
                        aInventoryMovement.get().getSku(),
                        aInventoryMovement.get().getQuantity(),
                        InventoryMovementStatus.IN
                ));
                this.inventoryMovementGateway.deleteById(aInventoryMovement.get().getId().getValue());

                return null;
            });

            if (aTransactionResult.isFailure()) {
                throw TransactionFailureException.with(aTransactionResult.getErrorResult());
            }
        }
    }
}
