package com.kaua.ecommerce.infrastructure.service;

public interface EventStorageService {

    void send(final Object event);
}
