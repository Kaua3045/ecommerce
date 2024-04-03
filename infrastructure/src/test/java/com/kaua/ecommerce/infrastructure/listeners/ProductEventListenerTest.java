package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.usecases.product.search.remove.RemoveProductUseCase;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.service.EventValidationService;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    @Value("${kafka.consumers.products.topics.[0]}")
    private String productTopic;

    @Value("${kafka.consumers.products.topics.[1]}")
    private String productCreatedTopic;

    @Value("${kafka.consumers.products.topics.[2]}")
    private String productUpdatedTopic;

    @Value("${kafka.consumers.products.topics.[3]}")
    private String productDeletedTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecord<String, String>> record;

    @AfterEach
    void clean() throws ExecutionException, InterruptedException, TimeoutException {
        final var aTopicsName = admin().listTopics().listings().get(1, TimeUnit.MINUTES).stream()
                .map(TopicListing::name)
                .filter(it -> it.contains("product"))
                .toList();

        cleanUpMessages("test-product-event-listener", aTopicsName);
    }

    @Test
    void givenAValidProductDeletedEvent_whenReceiveThrowsExceptionAndSendToDLT_shouldNotDeleteProduct() throws InterruptedException, ExecutionException, TimeoutException {
        final var expectedMaxAttempts = 4;
        final var expectedMaxDltAttempts = 1;
        final var expectedMainTopic = "product_deleted-topic";
        final var expectedRetry0Topic = "product_deleted-topic-retry-0";
        final var expectedRetry1Topic = "product_deleted-topic-retry-1";
        final var expectedRetry2Topic = "product_deleted-topic-retry-2";
        final var expectedDltTopic = "product_deleted-topic-retry-dlt";

        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductDeletedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);
        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(5);

        Mockito.doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("Error on remove product use case");
        }).when(removeProductUseCase).execute(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(productEventListener).onDltMessage(Mockito.any(), Mockito.any());

        final var aProducerRecord = createProducerRecord(productDeletedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        Mockito.verify(productEventListener, Mockito.times(expectedMaxAttempts)).onMessage(record.capture(), Mockito.any());

        final var allMetas = record.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        Mockito.verify(productEventListener, Mockito.times(expectedMaxDltAttempts)).onDltMessage(record.capture(), Mockito.any());

        Assertions.assertEquals(expectedDltTopic, record.getValue().topic());
    }

    @Test
    void givenAValidProductCreatedEvent_whenReceive_shouldPersistProduct() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductCreatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);
        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.doReturn(Optional.of(aProduct)).when(productGateway).findById(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aProduct;
        }).when(saveProductUseCase).execute(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(productCreatedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(1)).execute(eq(aProduct));
    }

    @Test
    void givenAnInvalidProductCreatedEvent_whenReceiveAsOccurredOnIsBefore_shouldNotProcessAndMarkAck() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductCreatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);
        aOutboxEvent.setOccurredOn(aOutboxEvent.getOccurredOn()
                .minus(1, TimeUnit.DAYS.toChronoUnit()));

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        final var aProducerRecord = createProducerRecord(productCreatedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(eq(aProduct));
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(productEventListener, Mockito.times(1))
                .onMessage(Mockito.any(), Mockito.any());
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(aProductEvent, aProduct.getId().getValue());
    }

    @Test
    void givenAValidProductUpdatedEvent_whenReceive_shouldUpdateProduct() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.doReturn(Optional.of(aProduct)).when(productGateway).findById(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return aProduct;
        }).when(saveProductUseCase).execute(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(productUpdatedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

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
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);
        aOutboxEvent.setOccurredOn(aOutboxEvent.getOccurredOn()
                .minus(1, TimeUnit.DAYS.toChronoUnit()));

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        final var aProducerRecord = createProducerRecord(productUpdatedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(productEventListener, Mockito.times(1))
                .onMessage(Mockito.any(), Mockito.any());
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(aProductEvent, aProduct.getId().getValue());
    }

    @Test
    void givenAValidProductDeletedEvent_whenReceive_shouldDeleteProduct() throws Exception {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductDeletedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(removeProductUseCase).execute(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(productDeletedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(removeProductUseCase, Mockito.times(1)).execute(aProduct.getId().getValue());
    }

    @Test
    void givenAnInvalidProductDeletedEventOccurredIsOld_whenReceiveButNotProcessThisSendDLT_shouldNotProcessProductDeletedEvent() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        final var aProduct = Product.newProduct(
                "Refactoring: Improving the Design of Existing Code",
                null,
                BigDecimal.valueOf(64.99),
                CategoryID.unique(),
                Set.of(Fixture.Products.
                        productAttributes("Refactoring: Improving the Design of Existing Code")));
        final var aProductEvent = ProductDeletedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        final var aProducerRecord = createProducerRecord(productDeletedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(Mockito.any(), Mockito.any());

        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothingAndNack() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        final var aOutboxEntity = OutboxEventEntity.create(new
                TestListenerDomainEvent(ProductID.unique().getValue()));
        final var aMessage = aOutboxEntity.getData();

        // when
        final var aProducerRecord = createProducerRecord(productTopic, aMessage, aOutboxEntity);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatchAndPass7Days_whenReceive_shouldSendToDLT() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        final var aMockedAck = Mockito.mock(Acknowledgment.class);

        final var aOutboxEntity = OutboxEventEntity.create(new
                TestListenerDomainEvent(ProductID.unique().getValue()));
        aOutboxEntity.setOccurredOn(aOutboxEntity.getOccurredOn().minus(7, ChronoUnit.DAYS));

        final var aMessage = aOutboxEntity.getData();

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(t -> {
            aLatch.countDown();
            return null;
        }).when(aMockedAck).acknowledge();

        final var aConsumerRecord = createConsumerRecord(productTopic, aMessage, aOutboxEntity);
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onMessage(aConsumerRecord, aMockedAck);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));

        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductCreatedEvent_whenReceiveButProductNotExists_shouldNotProcessProductCreatedEvent() {
        // given
        final var aMockedAck = Mockito.mock(Acknowledgment.class);

        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductCreatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        Mockito.doReturn(Optional.empty()).when(productGateway).findById(Mockito.any());
        Mockito.doAnswer(t -> {
            aLatch.countDown();
            return null;
        }).when(aMockedAck).nack(Mockito.any(Duration.class));

        // when
        final var aConsumerRecord = createConsumerRecord(productCreatedTopic, aMessage, aOutboxEvent);
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onMessage(aConsumerRecord, aMockedAck);

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductUpdatedEvent_whenReceiveButProductNotExists_shouldNotProcessProductUpdatedEvent() {
        // given
        final var aMockedAck = Mockito.mock(Acknowledgment.class);

        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        Mockito.doReturn(Optional.empty()).when(productGateway).findById(Mockito.any());
        Mockito.doAnswer(t -> {
            aLatch.countDown();
            return null;
        }).when(aMockedAck).nack(Mockito.any(Duration.class));

        // when
        final var aConsumerRecord = createConsumerRecord(productUpdatedTopic, aMessage, aOutboxEvent);
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onMessage(aConsumerRecord, aMockedAck);

        // then
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductUpdatedEvent_whenReceiveInErrorDLTButNotProcessThis_shouldNotProcessProductUpdatedEvent() {
        final var mockAcknowledgment = Mockito.mock(Acknowledgment.class);

        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(t -> {
            aLatch.countDown();
            return null;
        }).when(mockAcknowledgment).acknowledge();

        final var aConsumerRecord = createConsumerRecord(productUpdatedTopic, aMessage, aOutboxEvent);
        aConsumerRecord.headers().add("kafka_original-topic", productUpdatedTopic.getBytes());
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onDltMessage(aConsumerRecord, mockAcknowledgment);

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductTestEventSenderInProductTopic_whenReceiveInErrorDOLTButNotProcessThisAndSendToProductTopic_shouldNotProcessProductTestEvent() {
        final var mockAcknowledgment = Mockito.mock(Acknowledgment.class);

        // given
        final var aProductEvent = new TestListenerDomainEvent(IdUtils.generateWithoutDash());
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(t -> {
            aLatch.countDown();
            return null;
        }).when(mockAcknowledgment).acknowledge();

        final var aConsumerRecord = createConsumerRecord(productTopic, aMessage, aOutboxEvent);
        aConsumerRecord.headers().add("kafka_original-topic", productTopic.getBytes());
        final var aProductEventListener = new ProductEventListener(productGateway, saveProductUseCase, removeProductUseCase, kafkaTemplate, eventValidationService);
        aProductEventListener.onDltMessage(aConsumerRecord, mockAcknowledgment);

        // then
        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(removeProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidProductUpdatedEventOccurredIsOld_whenReceiveButNotProcessThisSendDLT_shouldNotProcessProductUpdatedEvent() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        final var aProduct = Fixture.Products.book();
        final var aProductEvent = ProductUpdatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.when(eventValidationService.isInvalid(aProductEvent, aProduct.getId().getValue()))
                .thenAnswer(t -> {
                    latch.countDown();
                    return true;
                });

        // when
        final var aProducerRecord = createProducerRecord(productUpdatedTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(eventValidationService, Mockito.times(1))
                .isInvalid(Mockito.any(), Mockito.any());

        Mockito.verify(productGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(saveProductUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductCreatedButNotContainsHeaders_whenReceiveAndSendToDlt_shouldNotProcessIt() throws Exception {
        // given
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aProduct = Fixture.Products.tshirt();
        final var aProductCreated = ProductCreatedEvent.from(aProduct);
        final var aOutboxEvent = OutboxEventEntity.create(aProductCreated);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aConsumerRecord = new ConsumerRecord<String, String>(productCreatedTopic, 1, 0, null, aMessage);
        aConsumerRecord.headers().add("event_occurred_on", aOutboxEvent.getOccurredOn().toString().getBytes());
        final var aProductListener = new ProductEventListener(
                productGateway,
                saveProductUseCase,
                removeProductUseCase,
                kafkaTemplate,
                eventValidationService
        );
        aProductListener.onMessage(aConsumerRecord, aMockAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(removeProductUseCase, Mockito.times(0))
                .execute(Mockito.any());
    }
}
