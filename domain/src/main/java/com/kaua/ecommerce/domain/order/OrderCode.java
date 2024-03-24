package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;

import java.time.LocalDateTime;

public class OrderCode extends ValueObject {

    private final String value;

    private OrderCode(final String value) {
        if (value == null || value.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("orderCode")));
        }
        this.value = value;
    }

    public static OrderCode create(final long sequence) {
        final var aNow = LocalDateTime.now();
        final var aYear = aNow.getYear();
        final var aSequenceFormattedWithEightZero = String.format("%09d", sequence + 1);
        final var aCode = String.format("%d%s", aYear, aSequenceFormattedWithEightZero);
        return new OrderCode(aCode);
    }

    public static OrderCode with(final String value) {
        return new OrderCode(value);
    }

    public String getValue() {
        return value;
    }
}
