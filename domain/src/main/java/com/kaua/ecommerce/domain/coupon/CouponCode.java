package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.ValueObject;

public class CouponCode extends ValueObject {

    private final String value;

    private CouponCode(final String value) {
        this.value = replaceInvalidCharacters(value);
    }

    public static CouponCode create(final String value) {
        return new CouponCode(value);
    }

    public String getValue() {
        return value;
    }

    private String replaceInvalidCharacters(final String aValue) {
        return (aValue != null && !aValue.isBlank()) ? aValue.replaceAll("\\s", "-")
                .replaceAll("[^a-zA-Z0-9-_]", "") : aValue;
    }
}
