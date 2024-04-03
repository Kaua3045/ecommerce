package com.kaua.ecommerce.infrastructure.service.local;

import com.kaua.ecommerce.infrastructure.service.EventProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryEventProducerServiceImpl implements EventProducerService {

    private static Logger LOG = LoggerFactory.getLogger(InMemoryEventProducerServiceImpl.class);

    public InMemoryEventProducerServiceImpl() {}

    @Override
    public void send(final Object message, final String topic) {
        LOG.info("Received message and send to topic: {}, {}", message, topic);
    }
}
