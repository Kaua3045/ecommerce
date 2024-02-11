package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.event.EventValidationEntity;
import com.kaua.ecommerce.infrastructure.outbox.event.EventValidationRepository;
import com.kaua.ecommerce.infrastructure.service.EventValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;

@CacheGatewayTest
public class RedisEventValidationServiceImplTest extends CacheTestConfiguration {

    @Autowired
    private EventValidationRepository eventValidationRepository;

    @Autowired
    private EventValidationService eventValidationService;

    @Test
    void givenFirstEvent_whenIsInvalid_thenShouldReturnFalse() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        // when
        Assertions.assertEquals(0, this.eventValidationRepository.count());

        final var aResult = this.eventValidationService.isInvalid(aDomainEvent, aPayloadId);

        // then
        Assertions.assertEquals(1, this.eventValidationRepository.count());
        Assertions.assertFalse(aResult);
    }

    @Test
    void givenEventAndContainsOldEventSaved_whenIsInvalid_thenShouldReturnFalse() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        this.eventValidationRepository.save(
                EventValidationEntity.from(aDomainEvent.aggregateName().concat("-")
                                .concat(aPayloadId),
                        aDomainEvent.occurredOn().minus(1, ChronoUnit.DAYS)));

        // when
        Assertions.assertEquals(1, this.eventValidationRepository.count());
        final var aResult = this.eventValidationService.isInvalid(aDomainEvent, aPayloadId);

        // then
        Assertions.assertFalse(aResult);

        Assertions.assertEquals(aDomainEvent.occurredOn(), this.eventValidationRepository.findById(
                aDomainEvent.aggregateName().concat("-").concat(aPayloadId)).get().getOccurredOn());
    }

    @Test
    void givenEventAndEventStoredIs7DaysInFuture_whenIsInvalid_thenShouldReturnFalse() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        final var aEntity = EventValidationEntity.from(
                aDomainEvent.aggregateName().concat("-").concat(aPayloadId),
                aDomainEvent.occurredOn().plus(1, ChronoUnit.DAYS)
        );
        aEntity.setId(aDomainEvent.aggregateName().concat("-").concat(aPayloadId));
        aEntity.setOccurredOn(aDomainEvent.occurredOn().plus(7, ChronoUnit.DAYS));

        this.eventValidationRepository.save(aEntity);

        // when
        Assertions.assertEquals(1, this.eventValidationRepository.count());

        final var aResult = this.eventValidationService.isInvalid(aDomainEvent, aPayloadId);

        // then
        Assertions.assertEquals(1, this.eventValidationRepository.count());
        Assertions.assertTrue(aResult);
        Assertions.assertEquals(aEntity.getId(), aDomainEvent.aggregateName()
                .concat("-").concat(aPayloadId));
    }

    @Test
    void givenAggregateNameAndPayloadId_whenInvalidate_thenShouldRemoveEventValidation() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());
        final var aPayloadId = aDomainEvent.id();

        final var aPayloadIdWithAggregate = aDomainEvent.aggregateName().concat("-").concat(aPayloadId);

        this.eventValidationRepository.save(
                EventValidationEntity.from(aDomainEvent.aggregateName().concat("-")
                                .concat(aPayloadId),
                        aDomainEvent.occurredOn().plus(1, ChronoUnit.DAYS)));

        // when
        Assertions.assertEquals(1, this.eventValidationRepository.count());

        this.eventValidationService.invalidate(aDomainEvent.aggregateName(), aPayloadId);

        // then
        Assertions.assertEquals(0, this.eventValidationRepository.count());
    }
}
