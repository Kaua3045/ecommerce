package com.kaua.ecommerce.infrastructure.listeners.models;

import com.kaua.ecommerce.domain.event.DomainEvent;

import java.time.Instant;

public record TestListenerDomainEvent(
        String id,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    public TestListenerDomainEvent(String id) {
        this(id, "test", "test-created", Instant.now());
    }
}
