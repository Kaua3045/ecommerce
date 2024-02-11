package com.kaua.ecommerce.infrastructure.outbox.event;

import org.springframework.data.repository.CrudRepository;

public interface EventValidationRepository extends CrudRepository<EventValidationEntity, String> {
}
