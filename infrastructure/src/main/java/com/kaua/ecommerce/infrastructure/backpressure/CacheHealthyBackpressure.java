package com.kaua.ecommerce.infrastructure.backpressure;

import org.springframework.boot.actuate.data.redis.RedisHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("cacheHealthyBackpressure")
public class CacheHealthyBackpressure implements Backpressure {

    private final RedisHealthIndicator cacheHealthIndicator;

    public CacheHealthyBackpressure(final RedisHealthIndicator cacheHealthIndicator) {
        this.cacheHealthIndicator = Objects.requireNonNull(cacheHealthIndicator);
    }

    @Override
    public boolean shouldWait() {
        return this.cacheHealthIndicator.health().getStatus() != Status.UP;
    }
}
