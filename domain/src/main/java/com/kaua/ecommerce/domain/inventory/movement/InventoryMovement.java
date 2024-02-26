package com.kaua.ecommerce.domain.inventory.movement;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;

public class InventoryMovement extends AggregateRoot<InventoryMovementID> {

    private InventoryID inventoryId;
    private String sku;
    private int quantity;
    private InventoryMovementStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private InventoryMovement(
            final InventoryMovementID aInventoryMovementID,
            final InventoryID aInventoryID,
            final String aSku,
            final int aQuantity,
            final InventoryMovementStatus aStatus,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aInventoryMovementID);
        this.inventoryId = aInventoryID;
        this.sku = aSku;
        this.quantity = aQuantity;
        this.status = aStatus;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static InventoryMovement newInventoryMovement(
            final InventoryID aInventoryID,
            final String aSku,
            final int aQuantity,
            final InventoryMovementStatus aStatus
    ) {
        final var aId = InventoryMovementID.unique();
        final var aNow = InstantUtils.now();
        return new InventoryMovement(aId, aInventoryID, aSku, aQuantity, aStatus, aNow, aNow);
    }

    public static InventoryMovement with(
            final String aInventoryMovementID,
            final String aInventoryID,
            final String aSku,
            final int aQuantity,
            final InventoryMovementStatus aStatus,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new InventoryMovement(
                InventoryMovementID.from(aInventoryMovementID),
                InventoryID.from(aInventoryID),
                aSku,
                aQuantity,
                aStatus,
                aCreatedAt,
                aUpdatedAt
        );
    }

    public static InventoryMovement with(final InventoryMovement aInventoryMovement) {
        return new InventoryMovement(
                aInventoryMovement.getId(),
                aInventoryMovement.getInventoryId(),
                aInventoryMovement.getSku(),
                aInventoryMovement.getQuantity(),
                aInventoryMovement.getStatus(),
                aInventoryMovement.getCreatedAt(),
                aInventoryMovement.getUpdatedAt()
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new InventoryMovementValidation(handler, this).validate();
    }

    public InventoryID getInventoryId() {
        return inventoryId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public InventoryMovementStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "InventoryMovement(" +
                "id=" + getId().getValue() +
                ", inventoryId='" + inventoryId.getValue() + '\'' +
                ", sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", status=" + status.name() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ')';
    }
}
