package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import com.kaua.ecommerce.infrastructure.service.EventStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MySQLEventStorageServiceImpl implements EventStorageService {

    public static final Logger log = LoggerFactory.getLogger(MySQLEventStorageServiceImpl.class);

    private final OutboxEventRepository outboxEventRepository;

    public MySQLEventStorageServiceImpl(final OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = Objects.requireNonNull(outboxEventRepository);
    }

    @Override
    public void send(final Object message) {
        if (message instanceof DomainEvent aEvent) {
            final var aOutboxEvent = OutboxEventEntity.create(aEvent);
            this.outboxEventRepository.save(aOutboxEvent);
            log.info("Event sent to database: {}", aOutboxEvent);
        } else {
            log.warn("Event does not implement DomainEvent: {}", message);
        }
    }
}
