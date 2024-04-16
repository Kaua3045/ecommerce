package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.exceptions.KafkaHeadersException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class EventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    protected EventListener(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    Instant getOccurredOnHeaderValue(final ConsumerRecord<String, String> message) {
        final var aOccurredOnHeader = getEventOccurredOnHeader(message);
        final var aOccurredOnString = new String(aOccurredOnHeader);
        return InstantUtils.parse(aOccurredOnString);
    }

    String getEventTypeHeaderValue(final ConsumerRecord<String, String> message) {
        final var aEventTypeHeader = getEventTypeHeader(message);
        return new String(aEventTypeHeader);
    }

    String getEventIdHeaderValue(final ConsumerRecord<String, String> message) {
        final var aEventIdHeader = getEventIdHeader(message);
        return new String(aEventIdHeader);
    }

    ProducerRecord<String, Object> createProducerRecordWithHeaders(final ConsumerRecord<String, String> message, final String topic) {
        final var aProducerRecord = new ProducerRecord<String, Object>(topic, message.value());
        message.headers().forEach(aProducerRecord.headers()::add);
        return aProducerRecord;
    }

    void handlePublishMessageTimeout(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Exception ex,
            final Logger log
    ) {
        log.error("Error on sending message to topic {} and nack message {}", topic, message, ex);
        ack.nack(Duration.ofSeconds(5));
    }

    void handlePublishMessageToRetryTopic(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Logger log
    ) {
        try {
            kafkaTemplate.send(createProducerRecordWithHeaders(message, topic)).get(1, TimeUnit.MINUTES);
            ack.acknowledge();
            final var aEventType = getEventTypeHeaderValue(message);
            final var aEventId = getEventIdHeaderValue(message);
            log.warn("Event [type:{}] [eventId:{}] sent to retry topic: {}, payload: {}",
                    aEventType,
                    aEventId,
                    topic,
                    message.value()
            );
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            handlePublishMessageTimeout(message, topic, ack, e, log);
        }
    }

    void handleMessagingNotContainsHeaders(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Logger log,
            final String errorMessage
    ) {
        message.headers().add("error_message", errorMessage.getBytes());
        try {
            kafkaTemplate.send(createProducerRecordWithHeaders(message, topic)).get(1, TimeUnit.MINUTES);
            ack.acknowledge();
            log.warn("Event sent to DLT topic: {}, because: {}, payload: {}",
                    topic,
                    errorMessage,
                    message.value()
            );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handlePublishMessageTimeout(message, topic, ack, e, log);
        }
    }

    void handleMessagingErrorAndSendToDlt(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Logger log,
            final String errorMessage
    ) {
        try {
            message.headers().add("error_message", errorMessage.getBytes());
            kafkaTemplate.send(createProducerRecordWithHeaders(message, topic)).get(1, TimeUnit.MINUTES);
            ack.acknowledge();

            final var aEventType = getEventTypeHeaderValue(message);
            final var aEventId = getEventIdHeaderValue(message);
            log.warn("Event [type:{}] [eventId:{}] sent to DLT topic: {}, because: {}, payload: {}",
                    aEventType,
                    aEventId,
                    topic,
                    errorMessage,
                    message.value()
            );
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            handlePublishMessageTimeout(message, topic, ack, e, log);
        }
    }

    void handleMessagingTypeNotSupported(
            final ConsumerRecord<String, String> message,
            final String eventType,
            final String dltTopic,
            final Acknowledgment ack,
            final Logger log
    ) {
        final var aOccurredOn = getOccurredOnHeaderValue(message);
        final var aDuration = Duration.between(aOccurredOn, InstantUtils.now());

        if (aDuration.toDays() >= 7) {
            log.warn("Event with type {} not supported after {} days, sent to DLT: {}", eventType, aDuration.toDays(), dltTopic);
            handleMessagingErrorAndSendToDlt(
                    message,
                    dltTopic,
                    ack,
                    log,
                    "Event type not supported after " + aDuration.toDays() + " days, eventType: " + eventType
            );
        } else {
            log.warn("Event type not supported: {}", eventType);
            ack.nack(Duration.ofSeconds(5));
        }
    }

    private byte[] getEventOccurredOnHeader(final ConsumerRecord<String, String> message) {
        return Optional.ofNullable(message.headers().lastHeader("event_occurred_on"))
                .map(Header::value)
                .orElseThrow(() -> KafkaHeadersException.with("event_occurred_on header not found"));
    }

    private byte[] getEventTypeHeader(final ConsumerRecord<String, String> message) {
        return Optional.ofNullable(message.headers().lastHeader("event_type"))
                .map(Header::value)
                .orElseThrow(() -> KafkaHeadersException.with("event_type header not found"));
    }

    private byte[] getEventIdHeader(final ConsumerRecord<String, String> message) {
        return Optional.ofNullable(message.headers().lastHeader("event_id"))
                .map(Header::value)
                .orElseThrow(() -> KafkaHeadersException.with("event_id header not found"));
    }
}
