package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotAcceptDuplicatedItemsException;
import com.kaua.ecommerce.domain.order.identifiers.OrderDeliveryID;
import com.kaua.ecommerce.domain.order.identifiers.OrderID;
import com.kaua.ecommerce.domain.order.identifiers.OrderPaymentID;
import com.kaua.ecommerce.domain.order.validations.OrderValidation;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Order extends AggregateRoot<OrderID> {

    private final OrderCode orderCode;
    private final OrderStatus orderStatus;
    private final Set<OrderItem> orderItems;
    private final String customerId;
    private final String couponCode;
    private final float couponPercentage;
    private final OrderDeliveryID orderDeliveryId;
    private final OrderPaymentID orderPaymentId;
    private final Instant createdAt;
    private BigDecimal totalAmount;
    private Instant updatedAt;

    private Order(
            final OrderID aOrderID,
            final long aVersion,
            final OrderCode aOrderCode,
            final OrderStatus aOrderStatus,
            final Set<OrderItem> aOrderItems,
            final BigDecimal aTotalAmount,
            final String aCustomerId,
            final String aCouponCode,
            final float aCouponPercentage,
            final OrderDeliveryID aOrderDeliveryId,
            final OrderPaymentID aOrderPaymentId,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aOrderID, aVersion);
        this.orderCode = aOrderCode;
        this.orderStatus = aOrderStatus;
        this.orderItems = aOrderItems;
        this.totalAmount = aTotalAmount;
        this.customerId = aCustomerId;
        this.couponCode = aCouponCode;
        this.couponPercentage = aCouponPercentage;
        this.orderDeliveryId = aOrderDeliveryId;
        this.orderPaymentId = aOrderPaymentId;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Order newOrder(
            final OrderCode aOrderCode,
            final String aCustomerId,
            final String aCouponCode,
            final float aCouponPercentage,
            final OrderDelivery aOrderDelivery,
            final OrderPaymentID aOrderPaymentId
    ) {
        final var aOrderId = OrderID.unique();
        final var aNow = InstantUtils.now();

        if (aOrderDelivery == null) {
            throw DomainException.with(new Error(CommonErrorMessage.nullMessage("orderDeliveryId")));
        }

        return new Order(
                aOrderId,
                0,
                aOrderCode,
                OrderStatus.WAITING_PAYMENT,
                new HashSet<>(),
                BigDecimal.ZERO,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery.getId(),
                aOrderPaymentId,
                aNow,
                aNow
        );
    }

    public static Order with(
            final String aOrderId,
            final long aVersion,
            final String aOrderCode,
            final OrderStatus aOrderStatus,
            final Set<OrderItem> aOrderItems,
            final BigDecimal aTotalAmount,
            final String aCustomerId,
            final String aCouponCode,
            final float aCouponPercentage,
            final String aOrderDeliveryId,
            final String aOrderPaymentId,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new Order(
                OrderID.from(aOrderId),
                aVersion,
                OrderCode.with(aOrderCode),
                aOrderStatus,
                aOrderItems,
                aTotalAmount,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                OrderDeliveryID.from(aOrderDeliveryId),
                OrderPaymentID.from(aOrderPaymentId),
                aCreatedAt,
                aUpdatedAt
        );
    }

    public static Order with(final Order aOrder) {
        return new Order(
                aOrder.getId(),
                aOrder.getVersion(),
                aOrder.getOrderCode(),
                aOrder.getOrderStatus(),
                aOrder.getOrderItems(),
                aOrder.getTotalAmount(),
                aOrder.getCustomerId(),
                aOrder.getCouponCode().orElse(null),
                aOrder.getCouponPercentage(),
                aOrder.getOrderDeliveryId(),
                aOrder.getOrderPaymentId(),
                aOrder.getCreatedAt(),
                aOrder.getUpdatedAt()
        );
    }

    public void calculateTotalAmount(final OrderDelivery aOrderDelivery) {
        this.totalAmount = this.orderItems.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = this.totalAmount.add(BigDecimal.valueOf(aOrderDelivery.getFreightPrice()));

        if (this.getCouponCode().isPresent()) {
            this.totalAmount = this.totalAmount.subtract(this.totalAmount
                    .multiply(BigDecimal.valueOf(this.couponPercentage / 100)));
        }
    }

    public void addItem(final OrderItem aOrderItem) {
        this.orderItems.stream()
                .filter(it -> it.getSku().equals(aOrderItem.getSku()))
                .findFirst()
                .ifPresentOrElse(
                        it -> {
                            throw NotAcceptDuplicatedItemsException.with(aOrderItem.getSku());
                        },
                        () -> this.orderItems.add(aOrderItem));
    }

    @Override
    public void validate(ValidationHandler handler) {
        new OrderValidation(handler, this).validate();
    }

    public OrderCode getOrderCode() {
        return orderCode;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Set<OrderItem> getOrderItems() {
        return Collections.unmodifiableSet(orderItems);
    }

    public String getCustomerId() {
        return customerId;
    }

    public Optional<String> getCouponCode() {
        return Optional.ofNullable(couponCode);
    }

    public float getCouponPercentage() {
        return couponPercentage;
    }

    public OrderDeliveryID getOrderDeliveryId() {
        return orderDeliveryId;
    }

    public OrderPaymentID getOrderPaymentId() {
        return orderPaymentId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Order(" +
                "id=" + getId().getValue() +
                ", orderCode=" + orderCode.getValue() +
                ", orderStatus=" + orderStatus.name() +
                ", orderItems=" + orderItems.size() +
                ", customerId='" + customerId + '\'' +
                ", couponCode='" + getCouponCode().orElse(null) + '\'' +
                ", couponPercentage=" + couponPercentage +
                ", orderDeliveryId=" + orderDeliveryId.getValue() +
                ", orderPaymentId=" + orderPaymentId.getValue() +
                ", totalAmount=" + getTotalAmount() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + getVersion() +
                ')';
    }
}
