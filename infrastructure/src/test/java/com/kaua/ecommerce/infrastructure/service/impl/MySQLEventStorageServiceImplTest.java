package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import com.kaua.ecommerce.infrastructure.service.EventDatabaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class MySQLEventDatabaseServiceImplTest {

    @Autowired
    private EventDatabaseService eventDatabaseService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    void givenAValidDomainEvent_whenCallSend_shouldPersistOutboxEvent() {
        final var aDomainEvent = CategoryCreatedEvent.from(Fixture.Categories.tech());

        Assertions.assertDoesNotThrow(() -> this.eventDatabaseService.send(aDomainEvent, null));

        final var aSavedOutboxEvent = this.outboxEventRepository.findAll().stream().findFirst().get();

        Assertions.assertNotNull(aSavedOutboxEvent.getEventId());
        Assertions.assertNotNull(aSavedOutboxEvent.getData());
        Assertions.assertEquals(aDomainEvent.aggregateName(), aSavedOutboxEvent.getAggregateName());
        Assertions.assertEquals(aDomainEvent.eventType(), aSavedOutboxEvent.getEventType());
        Assertions.assertEquals(aDomainEvent.occurredOn(), aSavedOutboxEvent.getOccurredOn());
        Assertions.assertEquals(1, this.outboxEventRepository.count());
    }

    @Test
    void givenAnInvalidDomainEvent_whenCallSend_shouldDoesNotPersistOutboxEvent() {
        final var aEvent = new TestSaveEvent(IdUtils.generate());

        Assertions.assertDoesNotThrow(() -> this.eventDatabaseService.send(aEvent, null));
        Assertions.assertEquals(0, this.outboxEventRepository.count());
    }

    record TestSaveEvent(String id) {
    }
}
