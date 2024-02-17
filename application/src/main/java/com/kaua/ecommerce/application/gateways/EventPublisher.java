package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.event.DomainEvent;

public interface EventPublisher {

    <T extends DomainEvent> void publish(T event);
}
