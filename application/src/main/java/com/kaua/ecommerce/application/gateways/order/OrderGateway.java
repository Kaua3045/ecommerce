package com.kaua.ecommerce.application.gateways.order;

import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.domain.order.OrderItem;

import java.util.Set;

public interface OrderGateway {

    Order create(Order order);

    long count();

    Set<OrderItem> createInBatch(Set<OrderItem> orderItems);
}
