package com.kaua.ecommerce.infrastructure.service;

public interface EventProducerService {

    void send(final Object message, final String topic);
}
