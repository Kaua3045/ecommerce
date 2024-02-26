package com.kaua.ecommerce.domain.inventory.movement;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class InventoryMovementID extends Identifier {

    private final String value;

    private InventoryMovementID(final String value) {
        this.value = Objects.requireNonNull(value, "'id' should not be null");
    }

    public static InventoryMovementID unique() {
        return new InventoryMovementID(IdUtils.generate());
    }

    public static InventoryMovementID from(final String value) {
        return new InventoryMovementID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InventoryMovementID that = (InventoryMovementID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
