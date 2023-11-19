package com.kaua.ecommerce.infrastructure.service.local;

import com.kaua.ecommerce.infrastructure.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

public class InMemoryEventServiceImpl implements EventService {

    private static Logger LOG = LoggerFactory.getLogger(InMemoryEventServiceImpl.class);

    public InMemoryEventServiceImpl() {}

    @Override
    public void send(final Object message, final String topic) {
        LOG.info("Received message and send to topic: {}, {}", message, topic);
    }
}
