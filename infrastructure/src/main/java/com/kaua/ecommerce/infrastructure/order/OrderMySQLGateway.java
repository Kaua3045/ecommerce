package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderGateway;
import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.domain.order.OrderItem;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntity;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
public class OrderMySQLGateway implements OrderGateway {

    private static final Logger log = LoggerFactory.getLogger(OrderMySQLGateway.class);

    private final OrderJpaEntityRepository orderJpaEntityRepository;
    private final OrderItemJpaEntityRepository orderItemJpaEntityRepository;

    public OrderMySQLGateway(
            final OrderJpaEntityRepository orderJpaEntityRepository,
            final OrderItemJpaEntityRepository orderItemJpaEntityRepository
    ) {
        this.orderJpaEntityRepository = Objects.requireNonNull(orderJpaEntityRepository);
        this.orderItemJpaEntityRepository = Objects.requireNonNull(orderItemJpaEntityRepository);
    }

    @Override
    public Order create(final Order order) {
        this.orderJpaEntityRepository.save(OrderJpaEntity.toEntity(order));
        log.info("inserted order: {}", order);
        return order;
    }

    @Override
    public long count() {
        return this.orderJpaEntityRepository.getNextSequence();
    }

    @Override
    public Set<OrderItem> createInBatch(Set<OrderItem> orderItems) {
        final var aOrderItemsEntity = orderItems.stream()
                .map(OrderItemJpaEntity::toEntity)
                .toList();

        final var aResult = this.orderItemJpaEntityRepository.saveAll(aOrderItemsEntity);

        log.info("inserted order items: {}", aResult.size());
        return orderItems;
    }
}
