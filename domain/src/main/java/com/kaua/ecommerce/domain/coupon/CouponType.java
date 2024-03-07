package com.kaua.ecommerce.domain.coupon;

import java.util.Arrays;
import java.util.Optional;

public enum CouponType {

    UNLIMITED,
    LIMITED;

    public static Optional<CouponType> of(final String value) {
        return Arrays.stream(CouponType.values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
