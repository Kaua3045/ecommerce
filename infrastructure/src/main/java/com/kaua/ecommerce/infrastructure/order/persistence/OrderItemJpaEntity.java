package com.kaua.ecommerce.infrastructure.order.persistence;

import com.kaua.ecommerce.domain.order.OrderItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Table(name = "orders_items")
@Entity
public class OrderItemJpaEntity {

    @Id
    private String id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    public OrderItemJpaEntity() {
    }

    private OrderItemJpaEntity(
            final String id,
            final String orderId,
            final String productId,
            final String sku,
            final int quantity,
            final BigDecimal price
    ) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemJpaEntity toEntity(final OrderItem aOrderItem) {
        return new OrderItemJpaEntity(
                aOrderItem.getOrderItemId(),
                aOrderItem.getOrderId(),
                aOrderItem.getProductId(),
                aOrderItem.getSku(),
                aOrderItem.getQuantity(),
                aOrderItem.getPrice()
        );
    }

    public OrderItem toDomain() {
        return OrderItem.with(
                getId(),
                getOrderId(),
                getProductId(),
                getSku(),
                getQuantity(),
                getPrice()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
