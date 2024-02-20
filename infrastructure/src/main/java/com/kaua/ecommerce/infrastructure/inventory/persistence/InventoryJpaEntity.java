package com.kaua.ecommerce.infrastructure.inventory.persistence;

import com.kaua.ecommerce.domain.inventory.Inventory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "inventories")
public class InventoryJpaEntity {

    @Id
    private String id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public InventoryJpaEntity() {
    }

    private InventoryJpaEntity(
            final String id,
            final String productId,
            final String sku,
            final int quantity,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InventoryJpaEntity toEntity(final Inventory inventory) {
        return new InventoryJpaEntity(
                inventory.getId().getValue(),
                inventory.getProductId(),
                inventory.getSku(),
                inventory.getQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }

    public Inventory toDomain() {
        return Inventory.with(
                getId(),
                getProductId(),
                getSku(),
                getQuantity(),
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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
