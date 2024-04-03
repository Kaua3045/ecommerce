package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.infrastructure.exceptions.InternalServerErrorException;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.service.EventProducerService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaEventProducerServiceImpl implements EventProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventProducerServiceImpl.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventProducerServiceImpl(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @Override
    public void send(final Object aMessage, final String topic) {
        try {
            final var aOutboxEvent = (OutboxEventEntity) aMessage;
            final var aProducerRecord = new ProducerRecord<String, Object>(topic, aOutboxEvent.getData());
            aProducerRecord.headers().add("event_type", aOutboxEvent.getEventType().getBytes());
            aProducerRecord.headers().add("event_occurred_on", aOutboxEvent.getOccurredOn().toString().getBytes());

            this.kafkaTemplate.send(aProducerRecord).get(1, TimeUnit.MINUTES);
            log.info("Event sent to topic {} payload: {} headers: {}", topic, aProducerRecord.value(), aProducerRecord.headers().toArray());
        } catch (final InterruptedException | ExecutionException | TimeoutException | RuntimeException ex) {
            log.error("Error sending message {} to topic {}", aMessage, topic);
            throw InternalServerErrorException.with("Error sending message to topic " + topic, ex);
        }
    }
}
