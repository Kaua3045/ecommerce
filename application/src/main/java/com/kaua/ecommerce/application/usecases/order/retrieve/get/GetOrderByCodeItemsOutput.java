package com.kaua.ecommerce.application.usecases.order.retrieve.get;

import com.kaua.ecommerce.domain.order.OrderItem;

import java.math.BigDecimal;

public record GetOrderByCodeItemsOutput(
        String orderItemId,
        String productId,
        String sku,
        int quantity,
        BigDecimal price
) {

    public static GetOrderByCodeItemsOutput from(final OrderItem aOrderItem) {
        return new GetOrderByCodeItemsOutput(
                aOrderItem.getOrderItemId(),
                aOrderItem.getProductId(),
                aOrderItem.getSku(),
                aOrderItem.getQuantity(),
                aOrderItem.getPrice()
        );
    }
}
