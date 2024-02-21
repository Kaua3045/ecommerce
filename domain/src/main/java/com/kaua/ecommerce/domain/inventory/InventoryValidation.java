package com.kaua.ecommerce.domain.inventory;

import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

public class InventoryValidation extends Validator {

    private final Inventory inventory;

    public InventoryValidation(final ValidationHandler handler, final Inventory inventory) {
        super(handler);
        this.inventory = inventory;
    }

    @Override
    public void validate() {
        this.checkProductIdConstraints();
        this.checkSkuConstraints();
        this.checkQuantityConstraints();
    }

    private void checkProductIdConstraints() {
        if (this.inventory.getProductId() == null || this.inventory.getProductId().trim().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("productId")));
        }
    }

    private void checkSkuConstraints() {
        if (this.inventory.getSku() == null || this.inventory.getSku().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("sku")));
        }
    }

    private void checkQuantityConstraints() {
        if (this.inventory.getQuantity() < 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("quantity", -1)));
        }
    }
}
