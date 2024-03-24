package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.gateways.order.*;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderUseCase;
import com.kaua.ecommerce.application.usecases.order.create.DefaultCreateOrderUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class OrderUseCaseConfig {

    private final OrderGateway orderGateway;
    private final OrderCouponGateway orderCouponGateway;
    private final OrderCustomerGateway orderCustomerGateway;
    private final OrderDeliveryGateway orderDeliveryGateway;
    private final OrderPaymentGateway orderPaymentGateway;
    private final OrderProductGateway orderProductGateway;
    private final OrderFreightGateway orderFreightGateway;
    private final TransactionManager transactionManager;

    public OrderUseCaseConfig(
            final OrderGateway orderGateway,
            final OrderCouponGateway orderCouponGateway,
            final OrderCustomerGateway orderCustomerGateway,
            final OrderDeliveryGateway orderDeliveryGateway,
            final OrderPaymentGateway orderPaymentGateway,
            final OrderProductGateway orderProductGateway,
            final OrderFreightGateway orderFreightGateway,
            final TransactionManager transactionManager
    ) {
        this.orderGateway = Objects.requireNonNull(orderGateway);
        this.orderCouponGateway = Objects.requireNonNull(orderCouponGateway);
        this.orderCustomerGateway = Objects.requireNonNull(orderCustomerGateway);
        this.orderDeliveryGateway = Objects.requireNonNull(orderDeliveryGateway);
        this.orderPaymentGateway = Objects.requireNonNull(orderPaymentGateway);
        this.orderProductGateway = Objects.requireNonNull(orderProductGateway);
        this.orderFreightGateway = Objects.requireNonNull(orderFreightGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase() {
        return new DefaultCreateOrderUseCase(
                orderGateway,
                orderCouponGateway,
                orderCustomerGateway,
                orderDeliveryGateway,
                orderPaymentGateway,
                orderProductGateway,
                orderFreightGateway,
                transactionManager
        );
    }
}
