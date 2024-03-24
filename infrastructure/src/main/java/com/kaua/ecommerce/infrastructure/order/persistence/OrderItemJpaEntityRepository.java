package com.kaua.ecommerce.infrastructure.order.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaEntityRepository extends JpaRepository<OrderItemJpaEntity, String> {
}
