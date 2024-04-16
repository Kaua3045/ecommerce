package com.kaua.ecommerce.infrastructure.event;

import com.kaua.ecommerce.domain.event.DomainEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Table(name = "events_metadata")
@Entity
public class EventMetadataJpaEntity {

    @Id
    private String id;

    @Column(name = "payload_identifier", nullable = false)
    private String payloadIdentifier;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "occurred_on", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant occurredOn;

    public EventMetadataJpaEntity() {
    }

    private EventMetadataJpaEntity(
            final String id,
            final String payloadIdentifier,
            final Long version,
            final Instant occurredOn
    ) {
        this.id = id;
        this.payloadIdentifier = payloadIdentifier;
        this.version = version;
        this.occurredOn = occurredOn;
    }

    public static EventMetadataJpaEntity from(final DomainEvent aDomainEvent, final String payloadIdentifier) {
        return new EventMetadataJpaEntity(
                aDomainEvent.eventId(),
                payloadIdentifier,
                aDomainEvent.version(),
                aDomainEvent.occurredOn()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayloadIdentifier() {
        return payloadIdentifier;
    }

    public void setPayloadIdentifier(String payloadIdentifier) {
        this.payloadIdentifier = payloadIdentifier;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(Instant occurredOn) {
        this.occurredOn = occurredOn;
    }
}
