package com.kaua.ecommerce.domain.exceptions;

import java.util.Collections;

public class ProductNotHaveMoreAttributesException extends DomainException {

    public ProductNotHaveMoreAttributesException() {
        super("Product can't have more than 20 attributes", Collections.emptyList());
    }
}
