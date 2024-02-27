package com.kaua.ecommerce.application.usecases.inventory.delete.remove;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;

import java.util.Objects;

public class DefaultRemoveInventoryBySkuUseCase extends RemoveInventoryBySkuUseCase {

    private final InventoryGateway inventoryGateway;
    private final InventoryMovementGateway inventoryMovementGateway;
    private final TransactionManager transactionManager;

    public DefaultRemoveInventoryBySkuUseCase(
            final InventoryGateway inventoryGateway,
            final InventoryMovementGateway inventoryMovementGateway,
            final TransactionManager transactionManager
    ) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
        this.inventoryMovementGateway = Objects.requireNonNull(inventoryMovementGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public void execute(String sku) {
        final var aInventory = this.inventoryGateway.findBySku(sku);

        if (aInventory.isPresent()) {
            final var aTransactionResult = this.transactionManager.execute(() -> {
                this.inventoryGateway.deleteBySku(sku);
                this.inventoryMovementGateway.create(InventoryMovement.newInventoryMovement(
                        aInventory.get().getId(),
                        aInventory.get().getSku(),
                        aInventory.get().getQuantity(),
                        InventoryMovementStatus.REMOVED
                ));
                return true;
            });

            if (aTransactionResult.isFailure()) {
                throw TransactionFailureException.with(aTransactionResult.getErrorResult());
            }
        }
    }
}
