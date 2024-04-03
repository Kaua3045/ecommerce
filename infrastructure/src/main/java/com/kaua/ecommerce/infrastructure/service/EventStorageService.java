package com.kaua.ecommerce.infrastructure.service;

public interface EventDatabaseService {

    void send(final Object event, final String topic);
}
