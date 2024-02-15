package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.usecases.product.search.remove.RemoveProductUseCase;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.listeners.models.MessageValue;
import com.kaua.ecommerce.infrastructure.listeners.models.Operation;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.listeners.models.ValuePayload;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.service.EventValidationService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;

public class ProductEventListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private SaveProductUseCase saveProductUseCase;

    @MockBean
    private ProductGateway productGateway;

    @MockBean
    private RemoveProductUseCase removeProductUseCase;

    @MockBean
    private EventValidationService eventValidationService;

    @SpyBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    private ProductEventListener productEventListener;

    @Value("${kafka.consumers.products.topics}")
    private String productTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecordMetadata> metadata;

    @Test
    void givenAValidProductDeletedEvent_whenReceiveThrowsExceptionAndSendToDLT_shouldNotDeleteProduct() throws InterruptedException {
        final var expectedMaxAttempts = 4;
        final var expectedMaxDltAttempts = 1;
        final var expectedMainTopic = "product-topic";
        final var expectedRetry0Topic = "product-topic-retry-0";
        final var expectedRetry1Topic = "product-topic-retry-1";
        final var expectedRetry2Topic = "product-topic-retry-2";
        final var expectedDltTopic = "product-topic-retry-dlt";

        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductDeletedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(5);

        Mockito.doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("Error on remove product use case");
        }).when(removeProductUseCase).execute(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(productEventListener).onDltMessage(Mockito.any(), Mockito.any(), Mockito.any());

        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        Mockito.verify(productEventListener, Mockito.times(expectedMaxAttempts)).onMessage(eq(aMessage), Mockito.any(), metadata.capture());

        final var allMetas = metadata.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        Mockito.verify(productEventListener, Mockito.times(expectedMaxDltAttempts)).onDltMessage(eq(aMessage), Mockito.any(), metadata.capture());

        Assertions.assertEquals(expectedDltTopic, metadata.getValue().topic());
    }

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
    void givenAnInvalidProductCreatedEvent_whenReceiveAsOccurredOnIsBefore_shouldNotProcessAndMarkAck() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductCreatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);
        aOutboxEvent.setOccurredOn(aOutboxEvent.getOccurredOn()
                .minus(1, TimeUnit.DAYS.toChronoUnit()));

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(eq(aProduct));
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(productEventListener, Mockito.times(1))
                .onMessage(eq(aMessage), Mockito.any(), Mockito.any());
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(aProductEvent, aProduct.getId().getValue());
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
        Mockito.verify(productGateway, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(1)).execute(eq(aProduct));
    }

    @Test
    void givenAnInvalidProductUpdatedEvent_whenReceiveAsOccurredOnIsBefore_shouldNotProcessAndMarkAck() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);
        aOutboxEvent.setOccurredOn(aOutboxEvent.getOccurredOn()
                .minus(1, TimeUnit.DAYS.toChronoUnit()));

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.UPDATE)));

        final var latch = new CountDownLatch(1);

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(eq(aProduct));
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(productEventListener, Mockito.times(1))
                .onMessage(eq(aMessage), Mockito.any(), Mockito.any());
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(aProductEvent, aProduct.getId().getValue());
    }

    @Test
    void givenAValidProductDeletedEvent_whenReceive_shouldDeleteProduct() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductDeletedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(removeProductUseCase).execute(Mockito.any());

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(removeProductUseCase, Mockito.times(1)).execute(aProduct.getId().getValue());
    }

    @Test
    void givenAnInvalidProductDeletedEventOccurredIsOld_whenReceiveButNotProcessThisSendDLT_shouldNotProcessProductDeletedEvent() throws InterruptedException {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductDeletedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(1);

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(aProductEvent, aProduct.getId().getValue());
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(eq("product-dlt-invalid"), eq(aMessage));

        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() {
        // given
        final var aOutboxEntity = OutboxEventEntity.from(new
                TestListenerDomainEvent(ProductID.unique().getValue()));
        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEntity, aOutboxEntity, aSource(), Operation.CREATE)));

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
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
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onMessage(aMessage, null, buildMetadata());

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
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onMessage(aMessage, null, buildMetadata());

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductUpdatedEvent_whenReceiveInErrorDLTButNotProcessThis_shouldNotProcessProductUpdatedEvent() {
        final var mockMetadata = Mockito.mock(ConsumerRecordMetadata.class);
        final var mockAcknowledgment = Mockito.mock(Acknowledgment.class);

        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.UPDATE)));

        // when
        Mockito.when(mockMetadata.topic()).thenReturn("product-topic");
        Mockito.when(mockMetadata.partition()).thenReturn(1);
        Mockito.when(mockMetadata.offset()).thenReturn(1L);
        Mockito.when(mockMetadata.timestampType()).thenReturn(TimestampType.CREATE_TIME);
        Mockito.when(mockMetadata.timestamp()).thenReturn(1L);
        Mockito.doNothing().when(mockAcknowledgment).acknowledge();

        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onDltMessage(aMessage, mockAcknowledgment, mockMetadata);

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidProductUpdatedEventOccurredIsOld_whenReceiveButNotProcessThisSendDLT_shouldNotProcessProductUpdatedEvent() throws InterruptedException {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.from(aProductEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.UPDATE)));

        final var latch = new CountDownLatch(1);

        Mockito.doReturn(Optional.of(aProduct)).when(productGateway).findById(Mockito.any());

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        producer().send(new ProducerRecord<>(productTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(aProductEvent, aProduct.getId().getValue());
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(eq("product-dlt-invalid"), eq(aMessage));

        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    private ConsumerRecordMetadata buildMetadata() {
        return new ConsumerRecordMetadata(
                new RecordMetadata(new TopicPartition("product-topic", 1), 0, 1, 1, 1, 1),
                TimestampType.CREATE_TIME
        );
    }
}
