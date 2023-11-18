package com.kaua.ecommerce.domain.event;

@FunctionalInterface
public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(T event, String topic);
}
