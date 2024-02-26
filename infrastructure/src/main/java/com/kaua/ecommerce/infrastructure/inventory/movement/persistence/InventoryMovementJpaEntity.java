package com.kaua.ecommerce.infrastructure.inventory.movement.persistence;

import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inventories_movements")
public class InventoryMovementJpaEntity {

    @Id
    private String id;

    @Column(name = "inventory_id", nullable = false)
    private String inventoryId;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private InventoryMovementStatus movementType;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public InventoryMovementJpaEntity() {
    }

    private InventoryMovementJpaEntity(
            final String id,
            final String inventoryId,
            final String sku,
            final int quantity,
            final InventoryMovementStatus movementType,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.sku = sku;
        this.quantity = quantity;
        this.movementType = movementType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InventoryMovementJpaEntity toEntity(final InventoryMovement inventoryMovement) {
        return new InventoryMovementJpaEntity(
                inventoryMovement.getId().getValue(),
                inventoryMovement.getInventoryId().getValue(),
                inventoryMovement.getSku(),
                inventoryMovement.getQuantity(),
                inventoryMovement.getStatus(),
                inventoryMovement.getCreatedAt(),
                inventoryMovement.getUpdatedAt()
        );
    }

    public InventoryMovement toDomain() {
        return InventoryMovement.with(
                getId(),
                getInventoryId(),
                getSku(),
                getQuantity(),
                getMovementType(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public InventoryMovementStatus getMovementType() {
        return movementType;
    }

    public void setMovementType(InventoryMovementStatus movementType) {
        this.movementType = movementType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
