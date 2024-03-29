package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderDeliveryGateway;
import com.kaua.ecommerce.domain.order.OrderDelivery;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderPaymentJpaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderDeliveryMySQLGateway implements OrderDeliveryGateway {

    private static final Logger log = LoggerFactory.getLogger(OrderDeliveryMySQLGateway.class);

    private final OrderDeliveryJpaEntityRepository orderDeliveryJpaEntityRepository;

    public OrderDeliveryMySQLGateway(final OrderDeliveryJpaEntityRepository orderDeliveryJpaEntityRepository) {
        this.orderDeliveryJpaEntityRepository = Objects.requireNonNull(orderDeliveryJpaEntityRepository);
    }

    @Override
    public OrderDelivery create(OrderDelivery orderDelivery) {
        this.orderDeliveryJpaEntityRepository.save(OrderDeliveryJpaEntity.toEntity(orderDelivery));
        log.info("inserted order delivery: {}", orderDelivery);
        return orderDelivery;
    }
}
