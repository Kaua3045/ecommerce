package com.kaua.ecommerce.infrastructure.order.persistence;

import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.domain.order.OrderItem;
import com.kaua.ecommerce.domain.order.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "orders")
@Entity
public class OrderJpaEntity {

    @Id
    private String id;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "coupon_percentage")
    private float couponPercentage;

    @Column(name = "order_delivery_id", nullable = false)
    private String orderDeliveryId;

    @Column(name = "order_payment_id", nullable = false)
    private String orderPaymentId;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "order_id")
    private Set<OrderItemJpaEntity> orderItems;

    @Version
    private long version;

    public OrderJpaEntity() {
    }

    private OrderJpaEntity(
            final String id,
            final String orderCode,
            final OrderStatus status,
            final String customerId,
            final BigDecimal totalPrice,
            final String couponCode,
            final float couponPercentage,
            final String orderDeliveryId,
            final String orderPaymentId,
            final Instant createdAt,
            final Instant updatedAt,
            final long version
    ) {
        this.id = id;
        this.orderCode = orderCode;
        this.status = status;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
        this.couponCode = couponCode;
        this.couponPercentage = couponPercentage;
        this.orderDeliveryId = orderDeliveryId;
        this.orderPaymentId = orderPaymentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = new HashSet<>();
        this.version = version;
    }

    public static OrderJpaEntity toEntity(final Order aOrder) {
        final var aEntity =  new OrderJpaEntity(
                aOrder.getId().getValue(),
                aOrder.getOrderCode().getValue(),
                aOrder.getOrderStatus(),
                aOrder.getCustomerId(),
                aOrder.getTotalAmount(),
                aOrder.getCouponCode().orElse(null),
                aOrder.getCouponPercentage(),
                aOrder.getOrderDeliveryId().getValue(),
                aOrder.getOrderPaymentId().getValue(),
                aOrder.getCreatedAt(),
                aOrder.getUpdatedAt(),
                aOrder.getVersion()
        );

        aOrder.getOrderItems().forEach(aEntity::addOrderItem);
        return aEntity;
    }

    public Order toDomain() {
        return Order.with(
                getId(),
                getVersion(),
                getOrderCode(),
                getStatus(),
                getOrderItems().stream().map(OrderItemJpaEntity::toDomain).collect(Collectors.toSet()),
                getTotalPrice(),
                getCustomerId(),
                getCouponCode(),
                getCouponPercentage(),
                getOrderDeliveryId(),
                getOrderPaymentId(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public void addOrderItem(final OrderItem aOrderItem) {
        this.orderItems.add(OrderItemJpaEntity.toEntity(aOrderItem));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public float getCouponPercentage() {
        return couponPercentage;
    }

    public void setCouponPercentage(float couponPercentage) {
        this.couponPercentage = couponPercentage;
    }

    public String getOrderDeliveryId() {
        return orderDeliveryId;
    }

    public void setOrderDeliveryId(String orderDeliveryId) {
        this.orderDeliveryId = orderDeliveryId;
    }

    public String getOrderPaymentId() {
        return orderPaymentId;
    }

    public void setOrderPaymentId(String orderPaymentId) {
        this.orderPaymentId = orderPaymentId;
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

    public Set<OrderItemJpaEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItemJpaEntity> orderItems) {
        this.orderItems = orderItems;
    }

    public long getVersion() {
        return version;
    }
}
