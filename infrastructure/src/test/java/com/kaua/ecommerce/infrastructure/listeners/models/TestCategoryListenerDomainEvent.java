package com.kaua.ecommerce.infrastructure.listeners.models;

import com.kaua.ecommerce.domain.event.DomainEvent;

import java.time.Instant;

public record TestCategoryListenerDomainEvent(
        String id,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    public TestCategoryListenerDomainEvent(String id) {
        this(id, "test", "test-created", Instant.now());
    }
}
