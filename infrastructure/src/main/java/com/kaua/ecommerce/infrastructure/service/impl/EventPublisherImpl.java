package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.infrastructure.service.EventDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EventPublisherImpl implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisherImpl.class);

    private final EventDatabaseService eventDatabaseService;

    public EventPublisherImpl(EventDatabaseService eventDatabaseService) {
        this.eventDatabaseService = Objects.requireNonNull(eventDatabaseService);
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        try {
            log.info("Event published: {}", event.getClass().getSimpleName());
            this.eventDatabaseService.send(event, "");
        } catch (final Exception e) {
            log.error("Error to publish event", e);
        }
    }
}
