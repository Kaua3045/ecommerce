package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.category.search.remove.DefaultRemoveCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.search.save.DefaultSaveCategoryUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryDeletedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryUpdatedEvent;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.listeners.models.MessageValue;
import com.kaua.ecommerce.infrastructure.listeners.models.Operation;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.listeners.models.ValuePayload;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.eq;

public class CategoryEventListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private DefaultSaveCategoryUseCase saveCategoryUseCase;

    @MockBean
    private DefaultRemoveCategoryUseCase removeCategoryUseCase;

    @Value("${kafka.consumers.categories.topics}")
    private String categoryTopic;

    @Test
    void givenAValidCategoryCreatedEvent_whenReceive_shouldPersistCategory() throws Exception {
        // given
        final var aCategory = Fixture.Categories.tech();
        final var aCategoryEvent = CategoryCreatedEvent.from(aCategory);
        final var aOutboxEvent = OutboxEventEntity.from(aCategoryEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aCategory;
        }).when(saveCategoryUseCase).execute(Mockito.any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, aMessage)).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(saveCategoryUseCase, Mockito.times(1)).execute(eq(aCategory));
    }

    @Test
    void givenAValidCategoryUpdatedEvent_whenReceive_shouldPersistCategory() throws Exception {
        // given
        final var aCategory = Fixture.Categories.home();
        final var aCategoryEvent = CategoryUpdatedEvent.from(aCategory);
        final var aOutboxEvent = OutboxEventEntity.from(aCategoryEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aCategory;
        }).when(saveCategoryUseCase).execute(Mockito.any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, aMessage)).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(saveCategoryUseCase, Mockito.times(1)).execute(eq(aCategory));
    }

    @Test
    void givenAValidCategoryDeletedEventWithRootCategoryId_whenReceive_shouldRemoveCategory() throws Exception {
        // given
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();
        final var aCategoryEvent = CategoryDeletedEvent.from(aId, null);
        final var aOutboxEvent = OutboxEventEntity.from(aCategoryEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(removeCategoryUseCase).execute(Mockito.any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, aMessage)).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(removeCategoryUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        final var aOutboxEntity = OutboxEventEntity.from(new
                TestListenerDomainEvent(CategoryID.unique().getValue()));
        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEntity, aOutboxEntity, aSource(), Operation.CREATE)));

        // when
        producer().send(new ProducerRecord<>(categoryTopic, aMessage)).get(1, TimeUnit.MINUTES);

        // then
        Mockito.verify(saveCategoryUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeCategoryUseCase, Mockito.times(0)).execute(Mockito.any());
    }
}
