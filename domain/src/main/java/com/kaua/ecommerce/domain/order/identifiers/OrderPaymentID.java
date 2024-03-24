package com.kaua.ecommerce.domain.order.identifiers;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class OrderPaymentID extends Identifier {

    private final String value;

    private OrderPaymentID(final String value) {
        this.value = Objects.requireNonNull(value, "'id' should not be null");
    }

    public static OrderPaymentID unique() {
        return new OrderPaymentID(IdUtils.generate());
    }

    public static OrderPaymentID from(final String value) {
        return new OrderPaymentID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrderPaymentID orderID = (OrderPaymentID) o;
        return Objects.equals(getValue(), orderID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
