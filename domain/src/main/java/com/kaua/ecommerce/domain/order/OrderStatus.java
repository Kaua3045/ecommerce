package com.kaua.ecommerce.domain.order;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {

    WAITING_PAYMENT,
    PAID,
    DELIVERED,
    SENT,
    CANCELED;

    public static Optional<OrderStatus> of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
