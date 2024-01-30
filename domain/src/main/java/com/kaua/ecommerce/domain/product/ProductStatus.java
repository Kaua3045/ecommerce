package com.kaua.ecommerce.domain.product;

import java.util.Arrays;
import java.util.Optional;

public enum ProductStatus {

    ACTIVE,
    INACTIVE,
    DELETED;

    public static Optional<ProductStatus> of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
