package com.kaua.ecommerce.infrastructure.order.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPaymentJpaEntityRepository extends JpaRepository<OrderPaymentJpaEntity, String> {
}
