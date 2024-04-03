package com.kaua.ecommerce.infrastructure.service;

public interface EventService {

    void send(final Object message, final String topic);
}
