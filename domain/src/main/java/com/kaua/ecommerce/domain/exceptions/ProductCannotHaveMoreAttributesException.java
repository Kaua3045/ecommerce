package com.kaua.ecommerce.domain.exceptions;

import java.util.Collections;

public class ProductCannotHaveMoreAttributesException extends DomainException {

    public ProductCannotHaveMoreAttributesException() {
        super("Product can't have more than 10 attributes", Collections.emptyList());
    }
}
