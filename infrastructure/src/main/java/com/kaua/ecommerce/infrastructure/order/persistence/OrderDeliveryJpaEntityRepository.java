package com.kaua.ecommerce.infrastructure.order.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDeliveryJpaEntityRepository extends JpaRepository<OrderDeliveryJpaEntity, String> {
}
