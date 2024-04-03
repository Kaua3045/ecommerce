package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.UnitTest;
import com.kaua.ecommerce.infrastructure.exceptions.InternalServerErrorException;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@UnitTest
public class KafkaEventProducerServiceImplTest {

    @Test
    void testSendMethodException() {
        KafkaTemplate<String, Object> kafkaTemplateMock = Mockito.mock(KafkaTemplate.class);
        KafkaEventProducerServiceImpl kafkaEventService = new KafkaEventProducerServiceImpl(kafkaTemplateMock);

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Simulated exception"));

        final var aTopic = "testTopic";
        final var aOutboxEvent = OutboxEventEntity.create(new TestListenerDomainEvent(IdUtils.generate()));

        final var aProducerRecord = new ProducerRecord<String, Object>(aTopic, aOutboxEvent.getData());
        aProducerRecord.headers().add("event_type", aOutboxEvent.getEventType().getBytes());
        aProducerRecord.headers().add("event_occurred_on", aOutboxEvent.getOccurredOn().toString().getBytes());

        Mockito.when(kafkaTemplateMock.send(aProducerRecord)).thenReturn(failedFuture);

        final var aException = Assertions.assertThrows(InternalServerErrorException.class,
                () -> kafkaEventService.send(aOutboxEvent, "testTopic"));

        Assertions.assertEquals("Error sending message to topic testTopic", aException.getMessage());

        Mockito.verify(kafkaTemplateMock).send(aProducerRecord);
    }

    @Test
    void testSendMethodSuccess() {
        KafkaTemplate<String, Object> kafkaTemplateMock = Mockito.mock(KafkaTemplate.class);
        KafkaEventProducerServiceImpl kafkaEventService = new KafkaEventProducerServiceImpl(kafkaTemplateMock);

        CompletableFuture<SendResult<String, Object>> successFuture = new CompletableFuture<>();
        successFuture.complete(Mockito.mock(SendResult.class));

        final var aTopic = "testTopic";
        final var aOutboxEvent = OutboxEventEntity.create(new TestListenerDomainEvent(IdUtils.generate()));

        final var aProducerRecord = new ProducerRecord<String, Object>(aTopic, aOutboxEvent.getData());
        aProducerRecord.headers().add("event_type", aOutboxEvent.getEventType().getBytes());
        aProducerRecord.headers().add("event_occurred_on", aOutboxEvent.getOccurredOn().toString().getBytes());

        Mockito.when(kafkaTemplateMock.send(aProducerRecord)).thenReturn(successFuture);

        Assertions.assertDoesNotThrow(() -> kafkaEventService.send(aOutboxEvent, aTopic));

        Mockito.verify(kafkaTemplateMock).send(aProducerRecord);
    }
}
