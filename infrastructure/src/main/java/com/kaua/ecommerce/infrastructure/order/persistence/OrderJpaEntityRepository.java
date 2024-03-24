package com.kaua.ecommerce.infrastructure.order.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderJpaEntityRepository extends JpaRepository<OrderJpaEntity, String> {

    @Query(value = "SELECT NEXTVAL('order_code_sequence')", nativeQuery = true)
    Long getNextSequence();
}
