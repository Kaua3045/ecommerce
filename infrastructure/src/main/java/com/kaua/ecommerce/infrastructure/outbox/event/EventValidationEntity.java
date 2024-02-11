package com.kaua.ecommerce.infrastructure.outbox.event;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@RedisHash(value = "event-validation")
public class EventValidationEntity implements Serializable {

    @Id
    private String id;

    private Instant occurredOn;

    public EventValidationEntity() {
    }

    private EventValidationEntity(final String id, final Instant occurredOn) {
        this.id = id;
        this.occurredOn = occurredOn;
    }

    public static EventValidationEntity from(final String id, final Instant occurredOn) {
        return new EventValidationEntity(id, occurredOn);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(Instant occurredOn) {
        this.occurredOn = occurredOn;
    }


}
