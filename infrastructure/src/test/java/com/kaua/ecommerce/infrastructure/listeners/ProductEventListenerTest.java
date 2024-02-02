package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.listeners.models.MessageValue;
import com.kaua.ecommerce.infrastructure.listeners.models.Operation;
import com.kaua.ecommerce.infrastructure.listeners.models.TestCategoryListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.listeners.models.ValuePayload;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;

public class ProductEventListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private SaveProductUseCase saveProductUseCase;

    @MockBean
    private ProductGateway productGateway;

    @Value("${kafka.consumers.products.topics}")
    private String productTopic;

    @Test
    void givenAValidProductCreatedEvent_whenReceive_shouldPersistProduct() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductCreatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);

        Mockito.doReturn(Optional.of(aProduct)).when(productGateway).findById(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aProduct;
        }).when(saveProductUseCase).execute(Mockito.any());

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(1)).execute(eq(aProduct));
    }

    @Test
    void givenAValidProductUpdatedEvent_whenReceive_shouldUpdateProduct() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.UPDATE)));

        final var latch = new CountDownLatch(1);

        Mockito.doReturn(Optional.of(aProduct)).when(productGateway).findById(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aProduct;
        }).when(saveProductUseCase).execute(Mockito.any());

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(1)).execute(eq(aProduct));
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() {
        // given
        final var aOutboxEntity = OutboxEventEntity.from(new
                TestCategoryListenerDomainEvent(CategoryID.unique().getValue()));
        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEntity, aOutboxEntity, aSource(), Operation.CREATE)));

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductCreatedEvent_whenReceiveButProductNotExists_shouldNotProcessProductCreatedEvent() {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductCreatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        Mockito.doReturn(Optional.empty()).when(productGateway).findById(Mockito.any());

        // when
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase);
        aProductEventListener.onMessage(aMessage, null);

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductUpdatedEvent_whenReceiveButProductNotExists_shouldNotProcessProductUpdatedEvent() {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.UPDATE)));

        Mockito.doReturn(Optional.empty()).when(productGateway).findById(Mockito.any());

        // when
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase);
        aProductEventListener.onMessage(aMessage, null);

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }
}
