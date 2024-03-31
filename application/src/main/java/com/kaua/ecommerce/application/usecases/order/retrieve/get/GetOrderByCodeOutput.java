package com.kaua.ecommerce.application.usecases.order.retrieve.get;

import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.domain.order.OrderDelivery;
import com.kaua.ecommerce.domain.order.OrderPayment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record GetOrderByCodeOutput(
        String orderId,
        String orderCode,
        String orderStatus,
        String customerId,
        String couponCode,
        float couponPercentage,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant updatedAt,
        long version,
        Set<GetOrderByCodeItemsOutput> orderItems,
        GetOrderByCodeDeliveryOutput orderDelivery,
        GetOrderByCodePaymentOutput orderPayment
) {

    public static GetOrderByCodeOutput from(
            final Order aOrder,
            final OrderDelivery aOrderDelivery,
            final OrderPayment aOrderPayment
    ) {
        return new GetOrderByCodeOutput(
                aOrder.getId().getValue(),
                aOrder.getOrderCode().getValue(),
                aOrder.getOrderStatus().name(),
                aOrder.getCustomerId(),
                aOrder.getCouponCode().orElse(null),
                aOrder.getCouponPercentage(),
                aOrder.getTotalAmount(),
                aOrder.getCreatedAt(),
                aOrder.getUpdatedAt(),
                aOrder.getVersion(),
                aOrder.getOrderItems().stream().map(GetOrderByCodeItemsOutput::from)
                        .collect(Collectors.toSet()),
                GetOrderByCodeDeliveryOutput.from(aOrderDelivery),
                GetOrderByCodePaymentOutput.from(aOrderPayment)
        );
    }
}
