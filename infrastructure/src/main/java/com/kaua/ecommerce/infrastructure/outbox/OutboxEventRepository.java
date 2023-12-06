package com.kaua.ecommerce.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {
}
