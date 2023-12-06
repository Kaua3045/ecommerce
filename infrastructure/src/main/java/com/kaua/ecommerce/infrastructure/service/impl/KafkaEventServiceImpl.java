package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.infrastructure.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

public class KafkaEventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventServiceImpl.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventServiceImpl(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @Override
    public void send(final Object message, final String topic) {
        final var aResult = this.kafkaTemplate.send(topic, message);

        aResult.exceptionally(exception -> {
            log.warn("Error sending message {} to topic {}", message, topic, exception);
            return null;
        });
    }
}
