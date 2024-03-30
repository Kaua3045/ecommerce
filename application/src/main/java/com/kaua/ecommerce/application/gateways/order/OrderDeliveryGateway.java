package com.kaua.ecommerce.application.gateways.order;

import com.kaua.ecommerce.domain.order.OrderDelivery;

import java.util.Optional;

public interface OrderDeliveryGateway {

    OrderDelivery create(OrderDelivery orderDelivery);

    Optional<OrderDelivery> findById(String id);
}
