package com.kaua.ecommerce.domain.inventory.movement;

import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

public class InventoryMovementValidation extends Validator {

    private final InventoryMovement inventoryMovement;

    public InventoryMovementValidation(
            final ValidationHandler handler,
            final InventoryMovement inventoryMovement
    ) {
        super(handler);
        this.inventoryMovement = inventoryMovement;
    }

    @Override
    public void validate() {
        this.checkInventoryIdConstraints();
        this.checkSkuConstraints();
        this.checkQuantityConstraints();
        this.checkStatusConstraints();
    }

    private void checkInventoryIdConstraints() {
        if (this.inventoryMovement.getInventoryId() == null) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullMessage("inventoryId")));
        }
    }

    private void checkSkuConstraints() {
        if (this.inventoryMovement.getSku() == null || this.inventoryMovement.getSku().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("sku")));
        }
    }

    private void checkQuantityConstraints() {
        if (this.inventoryMovement.getQuantity() <= 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("quantity", 0)));
        }
    }

    private void checkStatusConstraints() {
        if (this.inventoryMovement.getStatus() == null) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullMessage("status")));
        }
    }
}
