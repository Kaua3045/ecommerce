package com.kaua.ecommerce.domain;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.DomainEventPublisher;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Entity<ID extends Identifier> {

    private final ID id;
    private final List<DomainEvent> domainEvents;
    private final long version;

    protected Entity(final ID id, final List<DomainEvent> domainEvents, final long version) {
        this.id = Objects.requireNonNull(id, "'id' should not be null");
        this.domainEvents = new ArrayList<>(domainEvents == null ? Collections.emptyList() : domainEvents);
        this.version = version;
    }

    public abstract void validate(ValidationHandler handler);

    public ID getId() {
        return id;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void publishDomainEvent(final DomainEventPublisher eventPublisher, final String aTopic) {
        if (eventPublisher == null) {
            return;
        }

        getDomainEvents().forEach(event -> eventPublisher.publish(event, aTopic));

        this.domainEvents.clear();
    }

    public void publishDomainEvent(final DomainEventPublisher eventPublisher) {
        if (eventPublisher == null) {
            return;
        }

        // TODO: IN FUTURE REFACTOR THIS TO PUBLISH METHOD DOES NOT RECEIVE TOPIC
        getDomainEvents().forEach(event -> eventPublisher.publish(event, null));

        this.domainEvents.clear();
    }

    public void registerEvent(final DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    public long getVersion() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Entity<?> entity = (Entity<?>) o;
        return Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
