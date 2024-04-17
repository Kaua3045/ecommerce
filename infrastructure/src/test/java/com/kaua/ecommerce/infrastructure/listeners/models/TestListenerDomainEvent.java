package com.kaua.ecommerce.infrastructure.listeners.models;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;

public record TestListenerDomainEvent(
        String id,
        String eventId,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    public TestListenerDomainEvent(String id) {
        this(id, IdUtils.generate(), "test", "test-created", InstantUtils.now());
    }
}
