package com.kaua.ecommerce.domain.order.identifiers;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class OrderID extends Identifier {

    private final String value;

    private OrderID(final String value) {
        this.value = Objects.requireNonNull(value, "'id' should not be null");
    }

    public static OrderID unique() {
        return new OrderID(IdUtils.generate());
    }

    public static OrderID from(final String value) {
        return new OrderID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrderID orderID = (OrderID) o;
        return Objects.equals(getValue(), orderID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
