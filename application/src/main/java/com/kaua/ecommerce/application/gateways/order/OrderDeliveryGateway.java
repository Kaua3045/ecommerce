package com.kaua.ecommerce.application.gateways.order;

import com.kaua.ecommerce.domain.order.OrderDelivery;

public interface OrderDeliveryGateway {

    OrderDelivery create(OrderDelivery orderDelivery);
}
