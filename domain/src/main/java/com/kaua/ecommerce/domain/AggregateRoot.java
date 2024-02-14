package com.kaua.ecommerce.domain;

import com.kaua.ecommerce.domain.event.DomainEvent;

import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregateRoot(final ID id) {
        super(id, Collections.emptyList(), 0);
    }

    protected AggregateRoot(final ID id, final long version) {
        super(id, Collections.emptyList(), version);
    }

    protected AggregateRoot(final ID id, final List<DomainEvent> domainEvents) {
        super(id, domainEvents, 0);
    }

    protected AggregateRoot(final ID id, final List<DomainEvent> domainEvents, final long version) {
        super(id, domainEvents, version);
    }
}
