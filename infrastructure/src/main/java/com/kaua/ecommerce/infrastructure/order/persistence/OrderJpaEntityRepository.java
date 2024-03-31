package com.kaua.ecommerce.infrastructure.order.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderJpaEntityRepository extends JpaRepository<OrderJpaEntity, String> {

    Optional<OrderJpaEntity> findByOrderCode(String code);
}
