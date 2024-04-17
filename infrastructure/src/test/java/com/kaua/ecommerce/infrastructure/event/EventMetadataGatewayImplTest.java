package com.kaua.ecommerce.infrastructure.event;

import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;

@CacheGatewayTest
@EnableJpaRepositories(basePackages = "com.kaua.ecommerce.infrastructure")
public class EventMetadataGatewayImplTest extends CacheTestConfiguration {

    @Autowired
    private EventMetadataGateway eventMetadataGateway;

    @Autowired
    private EventMetadataJpaEntityRepository eventMetadataJpaEntityRepository;

    @Autowired
    private RedisTemplate<String, Number> redisTemplate;

    @BeforeEach
    void cleanUp() {
        this.redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void givenAValidEventMetadataJpaEntity_whenSave_thenShouldSave() {
        final var aDomainEvent = new EventMetadataDomainEventGateway(IdUtils.generateWithoutDash(), 0);
        final var aEventMetadataJpaEntity = EventMetadataJpaEntity.from(
                aDomainEvent,
                aDomainEvent.id()
        );

        Assertions.assertEquals(0, this.eventMetadataJpaEntityRepository.count());

        Assertions.assertDoesNotThrow(() -> this.eventMetadataGateway.save(aEventMetadataJpaEntity));

        final var aEventMetadataJpaEntitySaved = this.eventMetadataJpaEntityRepository.findById(aDomainEvent.eventId());

        Assertions.assertTrue(aEventMetadataJpaEntitySaved.isPresent());
        Assertions.assertEquals(aEventMetadataJpaEntity.getId(), aEventMetadataJpaEntitySaved.get().getId());
        Assertions.assertEquals(aEventMetadataJpaEntity.getVersion(), aEventMetadataJpaEntitySaved.get().getVersion());
        Assertions.assertEquals(aEventMetadataJpaEntity.getPayloadIdentifier(), aEventMetadataJpaEntitySaved.get().getPayloadIdentifier());
        Assertions.assertEquals(aEventMetadataJpaEntity.getOccurredOn(), aEventMetadataJpaEntitySaved.get().getOccurredOn());
    }

    @Test
    void givenAnEventId_whenExistsByEventId_thenShouldReturnTrue() {
        final var aDomainEvent = new EventMetadataDomainEventGateway(IdUtils.generateWithoutDash(), 0);
        final var aEventMetadataJpaEntity = EventMetadataJpaEntity.from(
                aDomainEvent,
                aDomainEvent.id()
        );

        Assertions.assertEquals(0, this.eventMetadataJpaEntityRepository.count());

        this.eventMetadataGateway.save(aEventMetadataJpaEntity);

        Assertions.assertEquals(1, this.eventMetadataJpaEntityRepository.count());

        Assertions.assertTrue(this.eventMetadataGateway.existsByEventId(aDomainEvent.eventId()));

        final var aResult = this.redisTemplate.opsForValue().get("event-metadata:eventId:" + aDomainEvent.eventId());

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aEventMetadataJpaEntity.getVersion().intValue(), aResult);
    }

    @Test
    void givenAnEventId_whenExistsByEventId_thenShouldReturnCached() {
        final var aDomainEvent = new EventMetadataDomainEventGateway(IdUtils.generateWithoutDash(), 0);
        final var aEventMetadataJpaEntity = EventMetadataJpaEntity.from(
                aDomainEvent,
                aDomainEvent.id()
        );

        Assertions.assertEquals(0, this.eventMetadataJpaEntityRepository.count());

        this.eventMetadataGateway.save(aEventMetadataJpaEntity);

        Assertions.assertEquals(1, this.eventMetadataJpaEntityRepository.count());

        Assertions.assertTrue(this.eventMetadataGateway.existsByEventId(aDomainEvent.eventId()));
        Assertions.assertTrue(this.eventMetadataGateway.existsByEventId(aDomainEvent.eventId()));
    }

    @Test
    void givenAEventWithSamePayloadIdButNewVersion_whenNewerEventProcessed_thenShouldReturnFalse() {
        final var aDomainEvent = new EventMetadataDomainEventGateway(IdUtils.generateWithoutDash(), 0);
        final var aEventMetadataJpaEntity = EventMetadataJpaEntity.from(
                aDomainEvent,
                aDomainEvent.id()
        );

        Assertions.assertEquals(0, this.eventMetadataJpaEntityRepository.count());

        this.eventMetadataGateway.save(aEventMetadataJpaEntity);

        Assertions.assertEquals(1, this.eventMetadataJpaEntityRepository.count());

        Assertions.assertFalse(this.eventMetadataGateway.newerEventProcessed(aDomainEvent.id(), aEventMetadataJpaEntity.getVersion() + 1));
    }

    @Test
    void givenAEventWithSamePayloadIdAndOldVersion_whenNewerEventProcessed_thenShouldReturnTrue() {
        final var aDomainEvent = new EventMetadataDomainEventGateway(IdUtils.generateWithoutDash(), 1);
        final var aEventMetadataJpaEntity = EventMetadataJpaEntity.from(
                aDomainEvent,
                aDomainEvent.id()
        );

        Assertions.assertEquals(0, this.eventMetadataJpaEntityRepository.count());

        this.eventMetadataGateway.save(aEventMetadataJpaEntity);

        Assertions.assertEquals(1, this.eventMetadataJpaEntityRepository.count());

        Assertions.assertTrue(this.eventMetadataGateway.newerEventProcessed(aDomainEvent.id(), aEventMetadataJpaEntity.getVersion() - 1));
    }

    @Test
    void givenAnEventId_whenDeleteByEventId_thenShouldDelete() {
        final var aDomainEvent = new EventMetadataDomainEventGateway(IdUtils.generateWithoutDash(), 0);
        final var aEventMetadataJpaEntity = EventMetadataJpaEntity.from(
                aDomainEvent,
                aDomainEvent.id()
        );

        Assertions.assertEquals(0, this.eventMetadataJpaEntityRepository.count());

        this.eventMetadataGateway.save(aEventMetadataJpaEntity);

        Assertions.assertEquals(1, this.eventMetadataJpaEntityRepository.count());

        this.eventMetadataGateway.deleteByEventId(aDomainEvent.eventId());

        Assertions.assertEquals(0, this.eventMetadataJpaEntityRepository.count());
    }

    private record EventMetadataDomainEventGateway(
            String id,
            String eventId,
            String aggregateName,
            String eventType,
            Instant occurredOn,
            long version
    ) implements DomainEvent {
        EventMetadataDomainEventGateway(String id, long version) {
            this(id, IdUtils.generateWithoutDash(), "test", "test", InstantUtils.now(), version);
        }
    }
}
