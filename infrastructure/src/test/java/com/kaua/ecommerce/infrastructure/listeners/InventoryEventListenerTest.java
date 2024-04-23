package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.inventory.events.InventoryCreatedRollbackBySkuEvent;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.backpressure.BackpressureFactory;
import com.kaua.ecommerce.infrastructure.event.EventMetadataGateway;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InventoryEventListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;

    @MockBean
    private EventMetadataGateway eventMetadataGateway;

    @MockBean
    private BackpressureFactory backpressureFactory;

    @SpyBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    private InventoryEventListener inventoryEventListener;

    @Value("${kafka.consumers.inventories.topics}")
    private String inventoryTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecord<String, String>> record;

    @BeforeEach
    void clean() throws ExecutionException, InterruptedException, TimeoutException {
        cleanUpMessages("inventory");
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkuEvent_whenReceiveThrowsExceptionAndSendToDLT_shouldResendToPrimaryTopic() throws InterruptedException, ExecutionException, TimeoutException {
        final var expectedMaxAttempts = 4;
        final var expectedMaxDltAttempts = 1;
        final var expectedMainTopic = "inventory-topic";
        final var expectedRetry0Topic = "inventory-topic-retry-0";
        final var expectedRetry1Topic = "inventory-topic-retry-1";
        final var expectedRetry2Topic = "inventory-topic-retry-2";
        final var expectedDltTopic = "inventory-topic-retry-dlt";

        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkuEvent
                .from(aInventory.getSku());
        final var aOutboxEvent = OutboxEventEntity.create(aInventoryCreatedRollbackEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(5);

        Mockito.doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("Error on remove inventory by sku use case");
        }).when(removeInventoryBySkuUseCase).execute(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(inventoryEventListener).onDltMessage(Mockito.any(), Mockito.any());

        final var aProducerRecord = createProducerRecord(inventoryTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        Mockito.verify(inventoryEventListener, Mockito.times(expectedMaxAttempts)).onMessage(record.capture(), Mockito.any());

        final var allMetas = record.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        Mockito.verify(inventoryEventListener, Mockito.times(expectedMaxDltAttempts)).onDltMessage(record.capture(), Mockito.any());

        Assertions.assertEquals(expectedDltTopic, record.getValue().topic());
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkusEvent_whenReceive_shouldRemoveInventories() throws Exception {
        // given
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkuEvent
                .from(aInventory.getSku());
        final var aOutboxEvent = OutboxEventEntity.create(aInventoryCreatedRollbackEvent);

        final var aMessage = aOutboxEvent.getData();

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(removeInventoryBySkuUseCase).execute(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(inventoryTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(1))
                .execute(Mockito.any());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        final var aMockedAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aOutboxEntity = OutboxEventEntity.create(new
                TestListenerDomainEvent(InventoryID.unique().getValue()));
        final var aMessage = aOutboxEntity.getData();

        final var aLatch = new CountDownLatch(1);

        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockedAcknowledgment).nack(Mockito.any(Duration.class));

        // when
        final var aConsumerRecord = createConsumerRecord(inventoryTopic, aMessage, aOutboxEntity);
        final var aInventoryEventListener = new InventoryEventListener(
                removeInventoryBySkuUseCase,
                kafkaTemplate,
                eventMetadataGateway,
                backpressureFactory
        );
        aInventoryEventListener.onMessage(aConsumerRecord, aMockedAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkuEvent_whenReceiveInDLTAndSendToRetryTopic_shouldResendToRetryTopic() throws Exception {
        // given
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkuEvent
                .from(aInventory.getSku());
        final var aOutboxEvent = OutboxEventEntity.create(aInventoryCreatedRollbackEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aConsumerRecord = createConsumerRecord(inventoryTopic, aMessage, aOutboxEvent);
        final var aInventoryEventListener = new InventoryEventListener(
                removeInventoryBySkuUseCase,
                kafkaTemplate,
                eventMetadataGateway,
                backpressureFactory
        );
        aInventoryEventListener.onDltMessage(aConsumerRecord, aMockAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(2, TimeUnit.MINUTES));
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(0))
                .execute(Mockito.any());
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkuEventButNotContainsHeaders_whenReceiveAndSendToDlt_shouldNotProcessIt() throws Exception {
        // given
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkuEvent
                .from(aInventory.getSku());
        final var aOutboxEvent = OutboxEventEntity.create(aInventoryCreatedRollbackEvent);
        aOutboxEvent.setOccurredOn(aOutboxEvent.getOccurredOn()
                .minus(11, ChronoUnit.DAYS));

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aConsumerRecord = new ConsumerRecord<String, String>(inventoryTopic, 1, 0, null, aMessage);
        aConsumerRecord.headers().add("event_id", aOutboxEvent.getEventId().getBytes());
        aConsumerRecord.headers().add("event_occurred_on", aOutboxEvent.getOccurredOn().toString().getBytes());
        final var aInventoryListener = new InventoryEventListener(
                removeInventoryBySkuUseCase,
                kafkaTemplate,
                eventMetadataGateway,
                backpressureFactory
        );
        aInventoryListener.onMessage(aConsumerRecord, aMockAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(0))
                .execute(Mockito.any());
    }

    @Test
    void givenAProcessedInventoryCreatedRollbackEvent_whenReceive_shouldNotProcessIt() throws Exception {
        // given
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkuEvent
                .from(aInventory.getSku());
        final var aOutboxEvent = OutboxEventEntity.create(aInventoryCreatedRollbackEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        Mockito.when(eventMetadataGateway.existsByEventId(Mockito.anyString()))
                .thenAnswer(t -> {
                    aLatch.countDown();
                    return true;
                });

        // when
        final var aProducerRecord = createProducerRecord(inventoryTopic, aMessage, aOutboxEvent);
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(0))
                .execute(Mockito.any());
    }

    @Test
    void givenAnInvalidEvent_whenReceiveButBackpressureActive_shouldNotProcess() throws InterruptedException {
        final var aMockedAck = Mockito.mock(Acknowledgment.class);

        final var aSku = Fixture.createSku("random");
        final var aInvalidEvent = new TestListenerDomainEvent(aSku);
        final var aOutboxEvent = OutboxEventEntity.create(aInvalidEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            aLatch.countDown();
            return true;
        }).when(backpressureFactory).shouldWait(Mockito.any());

        // when
        final var aConsumerRecord = createConsumerRecord(inventoryTopic, aMessage, aOutboxEvent);
        final var aInventoryEventListener = new InventoryEventListener(
                removeInventoryBySkuUseCase,
                kafkaTemplate,
                eventMetadataGateway,
                backpressureFactory
        );
        aInventoryEventListener.onMessage(aConsumerRecord, aMockedAck);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySku_whenReceiveButEventMetadataVersionIsSameAndThrows_shouldSendToErrorDlt() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        final var aMockedAck = Mockito.mock(Acknowledgment.class);

        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackBySkuEvent = InventoryCreatedRollbackBySkuEvent.from(aInventory.getSku());
        final var aOutboxEvent = OutboxEventEntity.create(aInventoryCreatedRollbackBySkuEvent);

        final var aMessage = aOutboxEvent.getData();

        final var aLatch = new CountDownLatch(2);

        Mockito.doAnswer(t -> {
            aLatch.countDown();
            throw new DataIntegrityViolationException("Error on save event metadata",
                    new ConstraintViolationException("Unique constraint", null, "idx_payload_identifier_version"));
        }).when(eventMetadataGateway).save(Mockito.any());

        Mockito.doAnswer(t -> {
            aLatch.countDown();
            return null;
        }).when(aMockedAck).acknowledge();

        // when
        final var aConsumerRecord = createConsumerRecord(inventoryTopic, aMessage, aOutboxEvent);
        final var aInventoryEventListener = new InventoryEventListener(
                removeInventoryBySkuUseCase,
                kafkaTemplate,
                eventMetadataGateway,
                backpressureFactory
        );
        aInventoryEventListener.onMessage(aConsumerRecord, aMockedAck);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(eventMetadataGateway, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(aMockedAck, Mockito.times(1)).acknowledge();
    }
}
