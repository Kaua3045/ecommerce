package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import com.kaua.ecommerce.infrastructure.service.EventDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MySQLEventDatabaseServiceImpl implements EventDatabaseService {

    public static final Logger log = LoggerFactory.getLogger(MySQLEventDatabaseServiceImpl.class);

    private final OutboxEventRepository outboxEventRepository;

    public MySQLEventDatabaseServiceImpl(final OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = Objects.requireNonNull(outboxEventRepository);
    }

    @Override
    public void send(final Object message, final String topic) {
        if (message instanceof DomainEvent aEvent) {
            this.outboxEventRepository.save(OutboxEventEntity.from(aEvent));
            log.debug("Event sent to database: {}", message);
        } else {
            log.warn("Event does not implement DomainEvent: {}", message);
        }
    }
}
