package com.kaua.ecommerce.infrastructure.backpressure;

import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.elasticsearch.ElasticsearchRestClientHealthIndicator;
import org.springframework.boot.actuate.health.Health;

@UnitTest
public class ElasticsearchHealthyBackpressureTest {

    @Test
    void testElasticsearchHealthyBackpressureShouldWaitReturnFalse() {
        final var elasticsearchRestClientHealthIndicator = Mockito.mock(ElasticsearchRestClientHealthIndicator.class);
        final var elasticsearchHealthyBackpressure = new ElasticsearchHealthyBackpressure(elasticsearchRestClientHealthIndicator);

        Mockito.when(elasticsearchRestClientHealthIndicator.health()).thenReturn(Health.up().build());

        Assertions.assertFalse(elasticsearchHealthyBackpressure.shouldWait());
    }

    @Test
    void testElasticsearchHealthyBackpressureShouldWaitReturnTrue() {
        final var elasticsearchRestClientHealthIndicator = Mockito.mock(ElasticsearchRestClientHealthIndicator.class);
        final var elasticsearchHealthyBackpressure = new ElasticsearchHealthyBackpressure(elasticsearchRestClientHealthIndicator);

        Mockito.when(elasticsearchRestClientHealthIndicator.health()).thenReturn(Health.down().build());

        Assertions.assertTrue(elasticsearchHealthyBackpressure.shouldWait());
    }
}
