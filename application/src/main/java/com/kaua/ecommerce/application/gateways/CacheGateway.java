package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.AggregateRoot;

import java.util.Optional;

public interface CacheGateway<T extends AggregateRoot> {

    void save(T aggregateRoot);

    Optional<T> get(String id);

    void delete(String id);
}
