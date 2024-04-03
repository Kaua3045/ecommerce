package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.category.search.remove.DefaultRemoveCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.search.save.DefaultSaveCategoryUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryDeletedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryUpdatedEvent;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
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
        final var aOutboxEvent = OutboxEventEntity.create(aCategoryEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aCategory;
        }).when(saveCategoryUseCase).execute(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(categoryTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(saveCategoryUseCase, Mockito.times(1)).execute(eq(aCategory));
    }

    @Test
    void givenAValidCategoryUpdatedEvent_whenReceive_shouldPersistCategory() throws Exception {
        // given
        final var aCategory = Fixture.Categories.home();
        final var aCategoryEvent = CategoryUpdatedEvent.from(aCategory);
        final var aOutboxEvent = OutboxEventEntity.create(aCategoryEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aCategory;
        }).when(saveCategoryUseCase).execute(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(categoryTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

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
        final var aOutboxEvent = OutboxEventEntity.create(aCategoryEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(removeCategoryUseCase).execute(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(categoryTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(removeCategoryUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        final var aOutboxEntity = OutboxEventEntity.create(new
                TestListenerDomainEvent(CategoryID.unique().getValue()));
        final var aMessage = aOutboxEntity.getData();

        // when
        final var aProducerRecord = createProducerRecord(categoryTopic, aMessage, aOutboxEntity);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        // then
        Mockito.verify(saveCategoryUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeCategoryUseCase, Mockito.times(0)).execute(Mockito.any());
    }
}
