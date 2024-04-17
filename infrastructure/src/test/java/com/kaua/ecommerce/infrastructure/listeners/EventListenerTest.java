package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.exceptions.KafkaHeadersException;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@IntegrationTest
public class EventListenerTest {

    private static final Logger log = LoggerFactory.getLogger(EventListenerTest.class);

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private Acknowledgment acknowledgment;

    @Test
    void givenAValidConsumerRecord_whenCallHandlePublishMessageToRetryTopic_shouldPublishMessageToRetryTopic() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult(
                        createProducerRecord(
                                aTopic,
                                aMessage,
                                aOutboxEntity
                        )
                )));
        Mockito.doNothing().when(acknowledgment).acknowledge();

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handlePublishMessageToRetryTopic(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log
        ));
    }

    @Test
    void givenAValidConsumerRecord_whenCallHandlePublishMessageToRetryTopicButThrowsTimeout_shouldPublishMessageToRetryTopic() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        Mockito.doAnswer(invocation -> {
            throw new TimeoutException();
        }).when(kafkaTemplate).send(Mockito.any(ProducerRecord.class));

        Mockito.doNothing().when(acknowledgment).nack(Mockito.any(Duration.class));

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handlePublishMessageToRetryTopic(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log
        ));
    }

    @Test
    void givenAValidConsumerRecord_whenCallHandleMessagingErrorAndSendToDlt_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "test-created-topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult(
                        createProducerRecord(
                                aTopic,
                                aMessage,
                                aOutboxEntity
                        )
                )));
        Mockito.doNothing().when(acknowledgment).acknowledge();

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingErrorAndSendToDlt(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log,
                "Newer event version is already processed"
        ));
    }

    @Test
    void givenAValidConsumerRecord_whenCallHandleMessagingErrorAndSendToDltButThrowsTimeout_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        Mockito.doAnswer(invocation -> {
            throw new TimeoutException();
        }).when(kafkaTemplate).send(Mockito.any(ProducerRecord.class));

        Mockito.doNothing().when(acknowledgment).nack(Mockito.any(Duration.class));

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingErrorAndSendToDlt(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log,
                "error"
        ));
    }

    @Test
    void givenAnInvalidConsumerWithoutEventIdHeader_whenCallGetEventIdHeaderValue_shouldThrowException() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        aConsumerRecord.headers().remove("event_id");

        Assertions.assertThrows(KafkaHeadersException.class, () -> aEventListenerHelper.getEventIdHeaderValue(aConsumerRecord));
    }

    @Test
    void givenAnInvalidConsumerWithoutEventTypeHeader_whenCallGetEventTypeHeaderValue_shouldThrowException() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        aConsumerRecord.headers().remove("event_type");

        Assertions.assertThrows(KafkaHeadersException.class, () -> aEventListenerHelper.getEventTypeHeaderValue(aConsumerRecord));
    }

    @Test
    void givenAnInvalidConsumerWithoutOccurredOnHeader_whenCallGetOccurredOnHeaderValue_shouldThrowException() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        aConsumerRecord.headers().remove("event_occurred_on");

        Assertions.assertThrows(KafkaHeadersException.class, () -> aEventListenerHelper.getOccurredOnHeaderValue(aConsumerRecord));
    }

    @Test
    void givenAValidConsumerRecordWithOccurredOnIsAfter7Days_whenCallHandleMessagingTypeNotSupported_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        final var aOccurredOn = Instant.now().minus(Duration.ofDays(8));
        aConsumerRecord.headers().remove("event_occurred_on");
        aConsumerRecord.headers().add("event_occurred_on", aOccurredOn.toString().getBytes());

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult(
                        createProducerRecord(
                                aTopic,
                                aMessage,
                                aOutboxEntity
                        )
                )));
        Mockito.doNothing().when(acknowledgment).acknowledge();

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingTypeNotSupported(
                aConsumerRecord,
                "event-type",
                aTopic,
                acknowledgment,
                log
        ));
    }

    @Test
    void givenAValidConsumerRecordWithOccurredOnIsBefore7Days_whenCallHandleMessagingTypeNotSupported_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        Mockito.doNothing().when(acknowledgment).acknowledge();

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingTypeNotSupported(
                aConsumerRecord,
                "event-type",
                aTopic,
                acknowledgment,
                log
        ));
    }

    @Test
    void givenValidConsumerRecordWithoutEventIdHeader_whenCallHandleMessagingNotContainsHeaders_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        aConsumerRecord.headers().remove("event_id");

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult(
                        createProducerRecord(
                                aTopic,
                                aMessage,
                                aOutboxEntity
                        )
                )));
        Mockito.doNothing().when(acknowledgment).acknowledge();

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingNotContainsHeaders(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log,
                "event_id header not found"
        ));
    }

    @Test
    void givenValidConsumerRecordWithoutEventTypeHeader_whenCallHandleMessagingNotContainsHeaders_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        aConsumerRecord.headers().remove("event_type");

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult(
                        createProducerRecord(
                                aTopic,
                                aMessage,
                                aOutboxEntity
                        )
                )));
        Mockito.doNothing().when(acknowledgment).acknowledge();

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingNotContainsHeaders(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log,
                "event_type header not found"
        ));
    }

    @Test
    void givenValidConsumerRecordWithoutOccurredOnHeader_whenCallHandleMessagingNotContainsHeaders_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        aConsumerRecord.headers().remove("event_occurred_on");

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult(
                        createProducerRecord(
                                aTopic,
                                aMessage,
                                aOutboxEntity
                        )
                )));
        Mockito.doNothing().when(acknowledgment).acknowledge();

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingNotContainsHeaders(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log,
                "event_occurred_on header not found"
        ));
    }

    @Test
    void givenAValidConsumerRecordWithoutOccurredOnHeader_whenCallHandleMessagingNotContainsHeadersButThrowsTimeout_shouldSendToDlt() {
        final var aEventListenerHelper = new EventListenerTestImpl(kafkaTemplate);

        final var aTopic = "topic";
        final var aOutboxEntity = OutboxEventEntity.create(new EventListenerTestDomainEvent("1"));

        final var aMessage = aOutboxEntity.getData();

        final var aConsumerRecord = createConsumerRecord(
                aTopic,
                aMessage,
                aOutboxEntity
        );

        aConsumerRecord.headers().remove("event_occurred_on");

        Mockito.doAnswer(invocation -> {
            throw new TimeoutException();
        }).when(kafkaTemplate).send(Mockito.any(ProducerRecord.class));

        Mockito.doNothing().when(acknowledgment).nack(Mockito.any(Duration.class));

        Assertions.assertDoesNotThrow(() -> aEventListenerHelper.handleMessagingNotContainsHeaders(
                aConsumerRecord,
                aTopic,
                acknowledgment,
                log,
                "event_occurred_on header not found"
        ));
    }

    private SendResult<String, String> buildSendResult(final ProducerRecord<String, String> aProducerRecord) {
        return new SendResult<>(aProducerRecord, null);
    }

    private ProducerRecord<String, String> createProducerRecord(
            final String aTopic,
            final String aMessage,
            final OutboxEventEntity aOutboxEntity
    ) {
        final var aProducerRecord = new ProducerRecord<String, String>(aTopic, aMessage);
        aProducerRecord.headers().add("event_id", aOutboxEntity.getEventId().getBytes());
        aProducerRecord.headers().add("event_type", aOutboxEntity.getEventType().getBytes());
        aProducerRecord.headers().add("event_occurred_on", aOutboxEntity.getOccurredOn().toString().getBytes());
        return aProducerRecord;
    }

    private ConsumerRecord<String, String> createConsumerRecord(
            final String aTopic,
            final String aMessage,
            final OutboxEventEntity aOutboxEntity
    ) {
        final var aConsumerRecord = new ConsumerRecord<String, String>(aTopic, 1, 0, null, aMessage);
        aConsumerRecord.headers().add("event_id", aOutboxEntity.getEventId().getBytes());
        aConsumerRecord.headers().add("event_type", aOutboxEntity.getEventType().getBytes());
        aConsumerRecord.headers().add("event_occurred_on", aOutboxEntity.getOccurredOn().toString().getBytes());
        return aConsumerRecord;
    }

    private record EventListenerTestDomainEvent(
            String id,
            String eventId,
            String aggregateName,
            String eventType,
            Instant occurredOn,
            long version
    ) implements DomainEvent {
        EventListenerTestDomainEvent(String id) {
            this(id, IdUtils.generate(), "aggregate-name", "event-type", InstantUtils.now(), 0);
        }
    }

    private static class EventListenerTestImpl extends EventListener {

        protected EventListenerTestImpl(KafkaTemplate<String, Object> kafkaTemplate) {
            super(kafkaTemplate);
        }
    }
}
