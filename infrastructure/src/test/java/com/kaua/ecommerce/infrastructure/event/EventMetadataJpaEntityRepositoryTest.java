package com.kaua.ecommerce.infrastructure.event;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.time.Instant;

@DatabaseGatewayTest
public class EventMetadataJpaEntityRepositoryTest {

    @Autowired
    private EventMetadataJpaEntityRepository eventMetadataJpaEntityRepository;

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.event.EventMetadataJpaEntity";

        final var aEventMetadata = new EventMetadataDomainEventRepository(IdUtils.generate(), 0);

        final var aEntity = EventMetadataJpaEntity.from(aEventMetadata, aEventMetadata.id());
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> eventMetadataJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullPayloadIdentifier_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "payloadIdentifier";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.event.EventMetadataJpaEntity.payloadIdentifier";

        final var aEventMetadata = new EventMetadataDomainEventRepository(IdUtils.generate(), 0);

        final var aEntity = EventMetadataJpaEntity.from(aEventMetadata, aEventMetadata.id());
        aEntity.setPayloadIdentifier(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> eventMetadataJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullVersion_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "version";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.event.EventMetadataJpaEntity.version";

        final var aEventMetadata = new EventMetadataDomainEventRepository(IdUtils.generate(), 0);

        final var aEntity = EventMetadataJpaEntity.from(aEventMetadata, aEventMetadata.id());
        aEntity.setVersion(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> eventMetadataJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullOccurredOn_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "occurredOn";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.event.EventMetadataJpaEntity.occurredOn";

        final var aEventMetadata = new EventMetadataDomainEventRepository(IdUtils.generate(), 0);

        final var aEntity = EventMetadataJpaEntity.from(aEventMetadata, aEventMetadata.id());
        aEntity.setOccurredOn(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> eventMetadataJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    private record EventMetadataDomainEventRepository(
            String id,
            String eventId,
            String aggregateName,
            String eventType,
            Instant occurredOn,
            long version
    ) implements DomainEvent {
        EventMetadataDomainEventRepository(String id, long version) {
            this(id, IdUtils.generateWithoutDash(), "test", "test", InstantUtils.now(), version);
        }
    }
}
