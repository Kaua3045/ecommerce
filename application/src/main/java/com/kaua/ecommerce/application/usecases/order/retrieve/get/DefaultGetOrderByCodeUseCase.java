package com.kaua.ecommerce.application.usecases.order.retrieve.get;

import com.kaua.ecommerce.application.gateways.order.OrderDeliveryGateway;
import com.kaua.ecommerce.application.gateways.order.OrderGateway;
import com.kaua.ecommerce.application.gateways.order.OrderPaymentGateway;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.domain.order.OrderDelivery;
import com.kaua.ecommerce.domain.order.OrderPayment;

import java.util.Objects;

public class DefaultGetOrderByCodeUseCase extends GetOrderByCodeUseCase {

    private final OrderGateway orderGateway;
    private final OrderDeliveryGateway orderDeliveryGateway;
    private final OrderPaymentGateway orderPaymentGateway;

    public DefaultGetOrderByCodeUseCase(
            final OrderGateway orderGateway,
            final OrderDeliveryGateway orderDeliveryGateway,
            final OrderPaymentGateway orderPaymentGateway
    ) {
        this.orderGateway = Objects.requireNonNull(orderGateway);
        this.orderDeliveryGateway = Objects.requireNonNull(orderDeliveryGateway);
        this.orderPaymentGateway = Objects.requireNonNull(orderPaymentGateway);
    }

    @Override
    public GetOrderByCodeOutput execute(String aOrderCode) {
        final var aOrder = this.orderGateway.findByCode(aOrderCode)
                .orElseThrow(NotFoundException.with(Order.class, aOrderCode));

        final var aOrderDelivery = this.orderDeliveryGateway.findById(aOrder.getOrderDeliveryId().getValue())
                .orElseThrow(NotFoundException.with(OrderDelivery.class, aOrder.getOrderDeliveryId().getValue()));

        final var aOrderPayment = this.orderPaymentGateway.findById(aOrder.getOrderPaymentId().getValue())
                .orElseThrow(NotFoundException.with(OrderPayment.class, aOrder.getOrderPaymentId().getValue()));

        return GetOrderByCodeOutput.from(aOrder, aOrderDelivery, aOrderPayment);
    }
}
