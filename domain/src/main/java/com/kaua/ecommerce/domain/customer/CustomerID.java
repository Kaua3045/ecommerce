package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class CustomerID extends Identifier {

    private final String value;

    private CustomerID(final String value) {
        this.value = Objects.requireNonNull(value, "'value' should not be null");
    }

    public static CustomerID unique() {
        return new CustomerID(IdUtils.generate());
    }

    public static CustomerID from(final String value) {
        return new CustomerID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CustomerID that = (CustomerID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
