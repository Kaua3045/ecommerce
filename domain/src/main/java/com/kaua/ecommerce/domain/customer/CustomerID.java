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
}
