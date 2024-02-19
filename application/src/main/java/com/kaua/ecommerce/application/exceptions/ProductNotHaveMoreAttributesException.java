package com.kaua.ecommerce.application.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;

import java.util.Collections;

public class ProductNotHaveMoreAttributesException extends DomainException {

    public ProductNotHaveMoreAttributesException() {
        super("Product not have more attributes", Collections.emptyList());
    }
}
