package com.kaua.ecommerce.application.gateways.order;

import com.kaua.ecommerce.domain.order.OrderPayment;

public interface OrderPaymentGateway {

    OrderPayment create(OrderPayment orderPayment);
}
