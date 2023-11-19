package com.kaua.ecommerce.domain;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.DomainEventPublisher;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

public class EntityTest {

    @Test
    void testValidEntityCreation() {
        SampleIdentifier sampleId = new SampleIdentifier(UUID.randomUUID().toString());
        Entity<SampleIdentifier> entity = createEntity(sampleId);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(sampleId, entity.getId());
    }

    @Test
    void testEntityEqualityAndHashCode() {
        final var uuid1 = UUID.randomUUID().toString();
        final var uuid2 = UUID.randomUUID().toString();
        SampleIdentifier id1 = new SampleIdentifier(uuid1);
        SampleIdentifier id2 = new SampleIdentifier(uuid1); // Same as id1
        SampleIdentifier id3 = new SampleIdentifier(uuid2);

        Entity<SampleIdentifier> entity1 = createEntity(id1);

        Entity<SampleIdentifier> entity2 = createEntity(id2);

        Entity<SampleIdentifier> entity3 = createEntity(id3);

        Assertions.assertEquals(entity1.getClass(), entity2.getClass());
        Assertions.assertEquals(entity1, entity1);
        Assertions.assertNotEquals(entity1, entity3);
        Assertions.assertNotEquals(entity1.hashCode(), entity2.hashCode());
        Assertions.assertNotEquals(entity1.hashCode(), entity3.hashCode());
        Assertions.assertFalse(entity1.equals(null));
        Assertions.assertFalse(entity1.equals(new Object()));
    }

    @Test
    void testInvalidEntityCreation() {
        SampleIdentifier sampleId = new SampleIdentifier(UUID.randomUUID().toString());
        Entity<SampleIdentifier> entity = createEntity(sampleId);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(sampleId, entity.getId());
        Assertions.assertNotEquals(null, entity);
        Assertions.assertNotEquals(entity, new Object());
    }

    @Test
    void testEntityRegisterEventAndPublishDomainEvent() {
        final var uuid1 = UUID.randomUUID().toString();
        SampleIdentifier id1 = new SampleIdentifier(uuid1);

        Entity<SampleIdentifier> entity1 = createEntity(id1);

        entity1.registerEvent(new SampleEntityEvent(uuid1));

        Assertions.assertEquals(1, entity1.getDomainEvents().size());

        entity1.publishDomainEvent(new SampleEntityPublisherEvent(), "topic");

        Assertions.assertEquals(0, entity1.getDomainEvents().size());
    }

    @Test
    void testEntityInvalidRegisterEventAndInvalidPublishDomainEvent() {
        final var uuid1 = UUID.randomUUID().toString();
        SampleIdentifier id1 = new SampleIdentifier(uuid1);

        Entity<SampleIdentifier> entity1 = createEntity(id1);

        entity1.registerEvent(null);

        Assertions.assertEquals(0, entity1.getDomainEvents().size());

        entity1.publishDomainEvent(null, null);

        Assertions.assertEquals(0, entity1.getDomainEvents().size());
    }

    static class SampleIdentifier extends Identifier {
        private final String value;

        public SampleIdentifier(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private Entity<SampleIdentifier> createEntity(SampleIdentifier id) {
        return new Entity<>(id, Collections.emptyList()) {
            @Override
            public void validate(ValidationHandler handler) {
                // Lógica de validação simulada
            }
        };
    }

    private record SampleEntityEvent(String id, String eventType, Instant occurredOn) implements DomainEvent {

        public SampleEntityEvent(final String id) {
            this(id, "test_created", InstantUtils.now());
        }
    }

    private class SampleEntityPublisherEvent implements DomainEventPublisher {

        @Override
        public <T extends DomainEvent> void publish(T event, String topic) {
            // Lógica de publicação de evento simulada
        }
    }
}
