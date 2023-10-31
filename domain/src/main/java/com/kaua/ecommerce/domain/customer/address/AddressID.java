package com.kaua.ecommerce.domain.customer.address;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class AddressID extends Identifier {

    private final String value;

    private AddressID(final String value) {
        this.value = Objects.requireNonNull(value, "'value' should not be null");
    }

    public static AddressID unique() {
        return new AddressID(IdUtils.generate());
    }

    public static AddressID from(final String value) {
        return new AddressID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AddressID that = (AddressID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
