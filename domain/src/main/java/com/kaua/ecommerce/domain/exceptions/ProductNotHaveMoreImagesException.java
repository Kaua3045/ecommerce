package com.kaua.ecommerce.domain.exceptions;

import java.util.Collections;

public class ProductNotHaveMoreImagesException extends DomainException {

    public ProductNotHaveMoreImagesException() {
        super("Product can't have more than 20 images", Collections.emptyList());
    }
}
