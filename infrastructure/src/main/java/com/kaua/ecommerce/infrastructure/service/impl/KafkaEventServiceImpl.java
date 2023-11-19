package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.infrastructure.service.EventService;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

public class KafkaEventServiceImpl implements EventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventServiceImpl(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @Override
    public void send(final Object message, final String topic) {
        this.kafkaTemplate.send(topic, message);
    }
}
