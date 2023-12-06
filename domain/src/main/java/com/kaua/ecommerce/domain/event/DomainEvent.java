package com.kaua.ecommerce.domain.event;

import java.io.Serializable;
import java.time.Instant;

public interface DomainEvent extends Serializable {

    String aggregateName();
    String eventType();
    Instant occurredOn();
}
