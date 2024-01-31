package com.kaua.ecommerce.application.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;

import java.util.Collections;

public class ProductIsDeletedException extends DomainException {

    public ProductIsDeletedException() {
        super("Product is deleted", Collections.emptyList());
    }
}
