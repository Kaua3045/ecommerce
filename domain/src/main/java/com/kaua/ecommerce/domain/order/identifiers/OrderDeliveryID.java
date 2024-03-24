package com.kaua.ecommerce.domain.order.identifiers;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class OrderDeliveryID extends Identifier {

    private final String value;

    private OrderDeliveryID(final String value) {
        this.value = Objects.requireNonNull(value, "'id' should not be null");
    }

    public static OrderDeliveryID unique() {
        return new OrderDeliveryID(IdUtils.generate());
    }

    public static OrderDeliveryID from(final String value) {
        return new OrderDeliveryID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrderDeliveryID orderID = (OrderDeliveryID) o;
        return Objects.equals(getValue(), orderID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
