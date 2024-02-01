package com.kaua.ecommerce.application.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class ProductIsDeletedException extends DomainException {

    protected ProductIsDeletedException(String aMessage, List<Error> aErrors) {
        super(aMessage, aErrors);
    }

    public static ProductIsDeletedException with(ProductID id) {
        final var aError = "Product with id %s is deleted".formatted(id.getValue());
        return new ProductIsDeletedException(aError, Collections.emptyList());
    }
}
