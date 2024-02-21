package com.kaua.ecommerce.domain.inventory;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class InventoryID extends Identifier {

    private final String value;

    private InventoryID(final String value) {
        this.value = Objects.requireNonNull(value, "'id' should not be null");
    }

    public static InventoryID unique() {
        return new InventoryID(IdUtils.generate());
    }

    public static InventoryID from(final String value) {
        return new InventoryID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InventoryID that = (InventoryID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
