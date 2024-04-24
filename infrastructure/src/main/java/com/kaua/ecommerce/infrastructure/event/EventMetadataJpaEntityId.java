package com.kaua.ecommerce.infrastructure.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;

@Embeddable
public class EventMetadataJpaEntityId implements Serializable {

    @Column(name = "id", nullable = false)
    private String eventId;

    @Column(name = "consumed_by", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventMetadataConsumer consumedBy;

    public EventMetadataJpaEntityId() {
    }

    private EventMetadataJpaEntityId(
            final String eventId,
            final EventMetadataConsumer consumedBy
    ) {
        this.eventId = eventId;
        this.consumedBy = consumedBy;
    }

    public static EventMetadataJpaEntityId from(
            final String eventId,
            final EventMetadataConsumer consumedBy
    ) {
        return new EventMetadataJpaEntityId(eventId, consumedBy);
    }

    public String getEventId() {
        return eventId;
    }

    public EventMetadataConsumer getConsumedBy() {
        return consumedBy;
    }
}
