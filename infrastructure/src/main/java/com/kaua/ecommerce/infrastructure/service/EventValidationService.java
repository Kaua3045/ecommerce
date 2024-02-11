package com.kaua.ecommerce.infrastructure.service;

import com.kaua.ecommerce.domain.event.DomainEvent;

public interface EventValidationService {

    <T extends DomainEvent> boolean isInvalid(final T event, final String payloadId);

    void invalidate(final String aggregateName, final String payloadId);
}
