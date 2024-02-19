package com.kaua.ecommerce.domain.exceptions;

import java.util.Collections;

public class ProductCannotHaveMoreImagesException extends DomainException {

    public ProductCannotHaveMoreImagesException() {
        super("Product can't have more than 20 images", Collections.emptyList());
    }
}
