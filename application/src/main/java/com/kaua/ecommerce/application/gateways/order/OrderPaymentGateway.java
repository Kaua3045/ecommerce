package com.kaua.ecommerce.application.gateways.order;

import com.kaua.ecommerce.domain.order.OrderPayment;

import java.util.Optional;

public interface OrderPaymentGateway {

    OrderPayment create(OrderPayment orderPayment);

    Optional<OrderPayment> findById(String id);
}
