package com.kaua.ecommerce.infrastructure.backpressure;

import org.springframework.boot.actuate.elasticsearch.ElasticsearchRestClientHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("elasticsearchHealthyBackpressure")
public class ElasticsearchHealthyBackpressure implements Backpressure {

    private final ElasticsearchRestClientHealthIndicator elasticsearchHealthIndicator;

    public ElasticsearchHealthyBackpressure(final ElasticsearchRestClientHealthIndicator elasticsearchHealthIndicator) {
        this.elasticsearchHealthIndicator = Objects.requireNonNull(elasticsearchHealthIndicator);
    }

    @Override
    public boolean shouldWait() {
        return this.elasticsearchHealthIndicator.health().getStatus() != Status.UP;
    }
}
