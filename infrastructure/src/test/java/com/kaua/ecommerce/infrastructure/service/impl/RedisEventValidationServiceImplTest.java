package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.service.EventValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.temporal.ChronoUnit;

@CacheGatewayTest
public class RedisEventValidationServiceImplTest extends CacheTestConfiguration {

    @Autowired
    private EventValidationService eventValidationService;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @Test
    void givenFirstEvent_whenIsInvalid_thenShouldReturnFalse() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        // when
        final var aResult = this.eventValidationService.isInvalid(aDomainEvent, aPayloadId);

        // then
        Assertions.assertFalse(aResult);
        Assertions.assertNotNull(this.redisTemplate.opsForValue().get(
                RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX.concat(aDomainEvent
                        .aggregateName().concat("-").concat(aPayloadId))));
    }

    @Test
    void givenEventAndContainsOldEventSaved_whenIsInvalid_thenShouldReturnFalse() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        this.redisTemplate.opsForValue().set(
                RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX.concat(aDomainEvent
                                .aggregateName().concat("-"))
                        .concat(aPayloadId), aDomainEvent.occurredOn().minus(1, ChronoUnit.DAYS).toEpochMilli());

        // when
        final var aResult = this.eventValidationService.isInvalid(aDomainEvent, aPayloadId);

        // then
        Assertions.assertFalse(aResult);

        Assertions.assertEquals(aDomainEvent.occurredOn().toEpochMilli(), this.redisTemplate.opsForValue()
                .get(RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX.concat(aDomainEvent
                        .aggregateName().concat("-").concat(aPayloadId))));
    }

    @Test
    void givenEventAndEventStoredIs7DaysInFuture_whenIsInvalid_thenShouldReturnTrue() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        this.redisTemplate.opsForValue().set(
                RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX.concat(aDomainEvent
                                .aggregateName().concat("-"))
                        .concat(aPayloadId), aDomainEvent.occurredOn().plus(7, ChronoUnit.DAYS).toEpochMilli());

        // when
        final var aResult = this.eventValidationService.isInvalid(aDomainEvent, aPayloadId);

        // then
        Assertions.assertTrue(aResult);
        Assertions.assertEquals(aDomainEvent.occurredOn().plus(7, ChronoUnit.DAYS).toEpochMilli(),
                this.redisTemplate.opsForValue().get(RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX
                        .concat(aDomainEvent.aggregateName().concat("-").concat(aPayloadId))));
    }

    @Test
    void givenAggregateNameAndPayloadId_whenInvalidate_thenShouldRemoveEventValidation() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        this.redisTemplate.opsForValue().set(
                RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX.concat(aDomainEvent
                                .aggregateName().concat("-"))
                        .concat(aPayloadId), aDomainEvent.occurredOn().plus(1, ChronoUnit.DAYS).toEpochMilli());

        // when
        Assertions.assertNotNull(this.redisTemplate.opsForValue().get(
                RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX.concat(aDomainEvent
                        .aggregateName().concat("-").concat(aPayloadId))));

        this.eventValidationService.invalidate(aDomainEvent.aggregateName(), aPayloadId);

        // then
        Assertions.assertNull(this.redisTemplate.opsForValue().get(
                RedisEventValidationServiceImpl.EVENT_VALIDATION_PREFIX.concat(aDomainEvent
                        .aggregateName().concat("-").concat(aPayloadId))));
    }
}
