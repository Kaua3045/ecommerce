package com.kaua.ecommerce.infrastructure.idempotency;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface IdempotencyKeyGateway {

    void save(String idempotencyKey, long ttl, TimeUnit timeUnit);

    void save(String idempotencyKey, Object body, long ttl, TimeUnit timeUnit);

    Optional<IdempotencyKeyOutput> get(String idempotencyKey);
}
