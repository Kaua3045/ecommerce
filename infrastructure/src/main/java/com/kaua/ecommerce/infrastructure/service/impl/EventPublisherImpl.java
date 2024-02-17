package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.infrastructure.service.EventDatabaseService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EventPublisherImpl implements EventPublisher {

    private final EventDatabaseService eventDatabaseService;

    public EventPublisherImpl(EventDatabaseService eventDatabaseService) {
        this.eventDatabaseService = Objects.requireNonNull(eventDatabaseService);
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        this.eventDatabaseService.send(event, "");
    }
}
