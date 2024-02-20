package com.kaua.ecommerce.domain.inventory;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;

public class Inventory extends AggregateRoot<InventoryID> {

    private String productId;
    private String sku;
    private int quantity;
    private Instant createdAt;
    private Instant updatedAt;

    private Inventory(
            final InventoryID aInventoryID,
            final String aProductId,
            final String aSku,
            final int aQuantity,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aInventoryID);
        this.productId = aProductId;
        this.sku = aSku;
        this.quantity = aQuantity;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Inventory newInventory(
            final String aProductId,
            final String aSku,
            final int aQuantity
    ) {
        final var aId = InventoryID.unique();
        final var aNow = InstantUtils.now();
        return new Inventory(aId, aProductId, aSku, aQuantity, aNow, aNow);
    }

    public static Inventory with(
            final String aInventoryID,
            final String aProductId,
            final String aSku,
            final int aQuantity,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new Inventory(InventoryID.from(aInventoryID), aProductId, aSku, aQuantity, aCreatedAt, aUpdatedAt);
    }

    public static Inventory with(final Inventory aInventory) {
        return new Inventory(
                aInventory.getId(),
                aInventory.getProductId(),
                aInventory.getSku(),
                aInventory.getQuantity(),
                aInventory.getCreatedAt(),
                aInventory.getUpdatedAt()
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new InventoryValidation(handler, this).validate();
    }

    public String getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Inventory(" +
                "id='" + getId().getValue() + '\'' +
                ", productId='" + productId + '\'' +
                ", sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ')';
    }
}
