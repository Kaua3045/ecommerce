package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.validation.Error;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem extends ValueObject {

    private final String orderItemId;
    private final String orderId;
    private final String productId;
    private final String sku;
    private final int quantity;
    private final BigDecimal price;

    private OrderItem(
            final String aOrderItemId,
            final String aOrderId,
            final String aProductId,
            final String aSku,
            final int aQuantity,
            final BigDecimal aPrice
    ) {
        this.orderItemId = aOrderItemId;
        this.orderId = aOrderId;
        this.productId = aProductId;
        this.sku = aSku;
        this.quantity = aQuantity;
        this.price = aPrice;
        selfValidate();
    }

    public static OrderItem create(
            final String aOrderId,
            final String aProductId,
            final String aSku,
            final int aQuantity,
            final BigDecimal aPrice
    ) {
        return new OrderItem(
                IdUtils.generate(),
                aOrderId,
                aProductId,
                aSku,
                aQuantity,
                aPrice
        );
    }

    public static OrderItem with(
            final String aOrderItemId,
            final String aOrderId,
            final String aProductId,
            final String aSku,
            final int aQuantity,
            final BigDecimal aPrice
    ) {
        return new OrderItem(
                aOrderItemId,
                aOrderId,
                aProductId,
                aSku,
                aQuantity,
                aPrice
        );
    }

    private void selfValidate() {
        if (orderId == null || orderId.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("orderId")));
        }
        if (productId == null || productId.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("productId")));
        }
        if (sku == null || sku.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("sku")));
        }
        if (quantity <= 0) {
            throw DomainException.with(new Error(CommonErrorMessage.greaterThan("quantity", 0)));
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw DomainException.with(new Error(CommonErrorMessage.greaterThan("price", 0)));
        }
    }

    public BigDecimal getTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getOrderId() {
        return orderId;
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

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrderItem orderItem = (OrderItem) o;
        return Objects.equals(getOrderItemId(), orderItem.getOrderItemId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderItemId());
    }
}
