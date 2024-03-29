package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderPaymentGateway;
import com.kaua.ecommerce.domain.order.OrderPayment;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderPaymentJpaEntity;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderPaymentJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderPaymentMySQLGateway implements OrderPaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(OrderPaymentMySQLGateway.class);

    private final OrderPaymentJpaEntityRepository orderPaymentJpaEntityRepository;

    public OrderPaymentMySQLGateway(final OrderPaymentJpaEntityRepository orderPaymentJpaEntityRepository) {
        this.orderPaymentJpaEntityRepository = Objects.requireNonNull(orderPaymentJpaEntityRepository);
    }

    @Override
    public OrderPayment create(OrderPayment orderPayment) {
        this.orderPaymentJpaEntityRepository.save(OrderPaymentJpaEntity.toEntity(orderPayment));
        log.info("inserted order payment: {}", orderPayment);
        return orderPayment;
    }
}
