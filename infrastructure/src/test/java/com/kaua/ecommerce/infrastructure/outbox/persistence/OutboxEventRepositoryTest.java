package com.kaua.ecommerce.infrastructure.outbox.persistence;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@IntegrationTest
public class OutboxEventRepositoryTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    void givenAnInvalidNullAggregateName_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "aggregateName";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity.aggregateName";

        final var aCategory = Fixture.Categories.tech();

        final var aEntity = OutboxEventEntity.from(CategoryCreatedEvent.from(aCategory));
        aEntity.setAggregateName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxEventRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullEventType_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "eventType";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity.eventType";

        final var aCategory = Fixture.Categories.tech();

        final var aEntity = OutboxEventEntity.from(CategoryCreatedEvent.from(aCategory));
        aEntity.setEventType(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxEventRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullData_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "data";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity.data";

        final var aCategory = Fixture.Categories.tech();

        final var aEntity = OutboxEventEntity.from(CategoryCreatedEvent.from(aCategory));
        aEntity.setData(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxEventRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullOccurredOn_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "occurredOn";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity.occurredOn";

        final var aCategory = Fixture.Categories.tech();

        final var aEntity = OutboxEventEntity.from(CategoryCreatedEvent.from(aCategory));
        aEntity.setOccurredOn(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxEventRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity";

        final var aCategory = Fixture.Categories.tech();

        final var aEntity = OutboxEventEntity.from(CategoryCreatedEvent.from(aCategory));
        aEntity.setEventId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> outboxEventRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
