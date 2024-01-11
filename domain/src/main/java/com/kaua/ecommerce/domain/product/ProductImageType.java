package com.kaua.ecommerce.domain.product;

import java.util.Arrays;
import java.util.Optional;

public enum ProductImageType {

    COVER,
    GALLERY,
    INVALID_TYPE;

    public static Optional<ProductImageType> of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
