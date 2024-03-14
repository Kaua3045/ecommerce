package com.kaua.ecommerce.domain.freight;

import java.util.Arrays;
import java.util.Optional;

public enum FreightType {

    SEDEX, PAC, UNKNOWN;

    public static Optional<FreightType> of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
