package com.kaua.ecommerce.infrastructure.outbox;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "outbox")
public class OutboxEventEntity {

    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "aggregate_name", nullable = false)
    private String aggregateName;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "data", nullable = false)
    private String data;

    @Column(name = "occurred_on", nullable = false)
    private Instant occurredOn;

    public OutboxEventEntity() {
    }

    public OutboxEventEntity(
            final String eventId,
            final String aggregateName,
            final String eventType,
            final String data,
            final Instant occurredOn
    ) {
        this.eventId = eventId;
        this.aggregateName = aggregateName;
        this.eventType = eventType;
        this.data = data;
        this.occurredOn = occurredOn;
    }

    public static OutboxEventEntity from(final DomainEvent aDomainEvent) {
        return new OutboxEventEntity(
                IdUtils.generate(),
                aDomainEvent.aggregateName(),
                aDomainEvent.eventType(),
                Json.writeValueAsString(aDomainEvent),
                aDomainEvent.occurredOn()
        );
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getAggregateName() {
        return aggregateName;
    }

    public void setAggregateName(String aggregateName) {
        this.aggregateName = aggregateName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(Instant occurredOn) {
        this.occurredOn = occurredOn;
    }
}
