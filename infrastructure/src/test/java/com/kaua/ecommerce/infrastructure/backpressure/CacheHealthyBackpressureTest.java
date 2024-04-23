package com.kaua.ecommerce.infrastructure.backpressure;

import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.backpressure.annotations.CacheHealthyBackpressureComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.data.redis.RedisHealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@CacheGatewayTest
public class CacheHealthyBackpressureTest extends CacheTestConfiguration {

    @Autowired
    @CacheHealthyBackpressureComponent
    private CacheHealthyBackpressure cacheHealthyBackpressure;

    @Test
    void testCacheHealthyBackpressureShouldWaitReturnFalse() {
        Assertions.assertFalse(cacheHealthyBackpressure.shouldWait());
    }

    @Test
    void testCacheHealthyBackpressureShouldWaitReturnTrue() {
        final var aMockedRedisConnection = Mockito.mock(RedisConnectionFactory.class);
        final var aCacheHealthyBackpressure = new CacheHealthyBackpressure(new RedisHealthIndicator(aMockedRedisConnection));
        Assertions.assertTrue(aCacheHealthyBackpressure.shouldWait());
    }
}
