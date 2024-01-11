package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

public class ProductValidation extends Validator {

    private final Product product;

    private static final int MINIMUM_LENGTH = 3;
    private static final int MAXIMUM_LENGTH = 255;

    protected ProductValidation(final Product product, final ValidationHandler handler) {
        super(handler);
        this.product = product;
    }

    @Override
    public void validate() {
        checkNameConstraints();
        checkDescriptionConstraints();
        checkPriceConstraints();
        checkQuantityConstraints();
        checkProductAttributesConstraints();
    }

    private void checkNameConstraints() {
        if (product.getName() == null || product.getName().isBlank()) {
            validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("name")));
            return;
        }

        if (product.getName().trim().length() < MINIMUM_LENGTH ||
                product.getName().trim().length() > MAXIMUM_LENGTH) {
            validationHandler().append(new Error(CommonErrorMessage.lengthBetween("name", MINIMUM_LENGTH, MAXIMUM_LENGTH)));
        }
    }

    private void checkDescriptionConstraints() {
        if (product.getDescription() != null &&
                product.getDescription().trim().length() > MAXIMUM_LENGTH) {
            validationHandler().append(new Error(CommonErrorMessage.lengthBetween("description", 0, MAXIMUM_LENGTH)));
        }
    }

    private void checkPriceConstraints() {
        if (product.getPrice() == null) {
            validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("price")));
            return;
        }

        if (product.getPrice().doubleValue() <= 0) {
            validationHandler().append(new Error(CommonErrorMessage.greaterThan("price", 0)));
        }
    }

    private void checkQuantityConstraints() {
        if (product.getQuantity() < 0) {
            validationHandler().append(new Error(CommonErrorMessage.greaterThan("quantity", -1)));
        }
    }

    private void checkProductAttributesConstraints() {
        product.getAttributes().forEach(attributes -> {
            if (attributes == null) {
                validationHandler().append(new Error("'attributes' must not be null"));
            }
        });
    }
}
