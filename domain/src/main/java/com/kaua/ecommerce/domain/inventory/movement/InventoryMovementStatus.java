package com.kaua.ecommerce.domain.inventory.movement;

import java.util.Arrays;
import java.util.Optional;

public enum InventoryMovementStatus {

    IN, OUT, REMOVED;

    public static Optional<InventoryMovementStatus> of(final String value) {
        return Arrays.stream(InventoryMovementStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
