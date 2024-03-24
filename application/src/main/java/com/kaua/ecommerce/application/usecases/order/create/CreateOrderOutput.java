package com.kaua.ecommerce.application.usecases.order.create;

import com.kaua.ecommerce.domain.order.Order;

public record CreateOrderOutput(String orderId, String orderCode) {

    public static CreateOrderOutput from(final Order aOrder) {
        return new CreateOrderOutput(aOrder.getId().getValue(), aOrder.getOrderCode().getValue());
    }
}
