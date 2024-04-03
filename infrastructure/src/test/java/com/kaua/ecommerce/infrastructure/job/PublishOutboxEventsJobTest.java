package com.kaua.ecommerce.infrastructure.job;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventStatus;
import com.kaua.ecommerce.infrastructure.service.EventProducerService;
import com.kaua.ecommerce.infrastructure.service.local.InMemoryEventProducerServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class PublishOutboxEventsJobTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private PublishOutboxEventsJob publishOutboxEventsJob;

    @Test
    void givenOutboxEvents_whenCallPublish_thenShouldPublishEvents() {
        final var aOutboxEvent = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        this.outboxEventRepository.saveAndFlush(aOutboxEvent);

        this.outboxEventRepository.saveAndFlush(OutboxEventEntity.create(
                ProductCreatedEvent.from(Fixture.Products.book())
        ));

        final var aOutboxEventProcessed = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        aOutboxEventProcessed.setStatus(OutboxEventStatus.PROCESSED);
        this.outboxEventRepository.saveAndFlush(aOutboxEventProcessed);

        Assertions.assertEquals(3, this.outboxEventRepository.count());

        this.publishOutboxEventsJob.publish();

        final var aOutboxEvents = this.outboxEventRepository.findAll();
        final var aOutboxEventsProcessedCount = aOutboxEvents.stream()
                .filter(aOutboxEventEntity -> aOutboxEventEntity.getStatus() == OutboxEventStatus.PROCESSED)
                .count();

        Assertions.assertEquals(3, aOutboxEventsProcessedCount);
    }

    @Test
    void givenOutboxEvents_whenCallPublish_thenShouldNotPublishEvents() {
        final var aOutboxEvent = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        aOutboxEvent.setStatus(OutboxEventStatus.PROCESSED);
        this.outboxEventRepository.saveAndFlush(aOutboxEvent);

        final var aOutboxEventProcessed = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        aOutboxEventProcessed.setStatus(OutboxEventStatus.PROCESSED);
        this.outboxEventRepository.saveAndFlush(aOutboxEventProcessed);

        Assertions.assertEquals(2, this.outboxEventRepository.count());

        this.publishOutboxEventsJob.publish();

        final var aOutboxEvents = this.outboxEventRepository.findAll();
        final var aOutboxEventsProcessedCount = aOutboxEvents.stream()
                .filter(aOutboxEventEntity -> aOutboxEventEntity.getStatus() == OutboxEventStatus.PROCESSED)
                .count();

        Assertions.assertEquals(2, aOutboxEventsProcessedCount);
    }

    @Test
    void givenOutboxEvents_whenCallPublishAndOnSendFailure_thenShouldNotPublishEvents() {
        final var aMockedEventService = Mockito.mock(EventProducerService.class);
        final var aPublishOutboxEventsJob = new PublishOutboxEventsJob(
                aMockedEventService,
                outboxEventRepository,
                transactionManager
        );

        final var aOutboxEvent = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        this.outboxEventRepository.saveAndFlush(aOutboxEvent);

        final var aOutboxEventProcessed = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        aOutboxEventProcessed.setStatus(OutboxEventStatus.PROCESSED);
        this.outboxEventRepository.saveAndFlush(aOutboxEventProcessed);

        Mockito.doThrow(new RuntimeException("Error sending message to topic"))
                .when(aMockedEventService).send(Mockito.any(), Mockito.anyString());

        Assertions.assertEquals(2, this.outboxEventRepository.count());

        Assertions.assertDoesNotThrow(aPublishOutboxEventsJob::publish);

        final var aOutboxEvents = this.outboxEventRepository.findAll();
        final var aOutboxEventsFailedCount = aOutboxEvents.stream()
                .filter(aOutboxEventEntity -> aOutboxEventEntity.getStatus() == OutboxEventStatus.FAILED)
                .count();

        Assertions.assertEquals(1, aOutboxEventsFailedCount);
    }

    @Test
    void givenOutboxEvents_whenCallPublishButTransactionThrows_thenShouldNotPublishEvents() {
        final var aMockedOutboxEventRepository = Mockito.mock(OutboxEventRepository.class);
        final var aPublishOutboxEventsJob = new PublishOutboxEventsJob(
                new InMemoryEventProducerServiceImpl(),
                aMockedOutboxEventRepository,
                transactionManager
        );

        final var aOutboxEvent = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        this.outboxEventRepository.saveAndFlush(aOutboxEvent);

        final var aOutboxEventProcessed = OutboxEventEntity
                .create(new TestListenerDomainEvent(IdUtils.generateWithoutDash()));
        aOutboxEventProcessed.setStatus(OutboxEventStatus.PROCESSED);
        this.outboxEventRepository.saveAndFlush(aOutboxEventProcessed);

        Mockito.doThrow(new RuntimeException("Error sending message to topic"))
                .when(aMockedOutboxEventRepository)
                .findTop50ByStatusOrderByOccurredOnAsc(OutboxEventStatus.PENDING);

        Assertions.assertEquals(2, this.outboxEventRepository.count());

        Assertions.assertDoesNotThrow(aPublishOutboxEventsJob::publish);

        final var aOutboxEvents = this.outboxEventRepository.findAll();
        final var aOutboxEventsFailedCount = aOutboxEvents.stream()
                .filter(aOutboxEventEntity -> aOutboxEventEntity.getStatus() == OutboxEventStatus.FAILED)
                .count();
        final var aOutboxEventsProcessedCount = aOutboxEvents.stream()
                .filter(aOutboxEventEntity -> aOutboxEventEntity.getStatus() == OutboxEventStatus.PROCESSED)
                .count();
        final var aOutboxEventsPendingCount = aOutboxEvents.stream()
                .filter(aOutboxEventEntity -> aOutboxEventEntity.getStatus() == OutboxEventStatus.PENDING)
                .count();

        Assertions.assertEquals(0, aOutboxEventsFailedCount);
        Assertions.assertEquals(1, aOutboxEventsProcessedCount);
        Assertions.assertEquals(1, aOutboxEventsPendingCount);
    }
}
