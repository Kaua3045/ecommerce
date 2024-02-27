package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.inventory.events.InventoryCreatedRollbackBySkusEvent;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.listeners.models.MessageValue;
import com.kaua.ecommerce.infrastructure.listeners.models.Operation;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.listeners.models.ValuePayload;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.record.TimestampType;
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
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;

public class InventoryEventListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;

    @SpyBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    private InventoryEventListener inventoryEventListener;

    @Value("${kafka.consumers.inventories.topics}")
    private String inventoryTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecordMetadata> metadata;

    @AfterEach
    void clean() {
        final var aTopicsList = List.of(
                "inventory-topic",
                "inventory-topic-retry-0",
                "inventory-topic-retry-1",
                "inventory-topic-retry-2",
                "inventory-topic-retry-dlt",
                "inventory-dlt-invalid"
        );

        cleanUpMessages("test-inventory-event-listener", aTopicsList);
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkusEvent_whenReceiveThrowsExceptionAndSendToDLT_shouldResendToPrimaryTopic() throws InterruptedException, ExecutionException {
        final var expectedMaxAttempts = 4;
        final var expectedMaxDltAttempts = 1;
        final var expectedMainTopic = "inventory-topic";
        final var expectedRetry0Topic = "inventory-topic-retry-0";
        final var expectedRetry1Topic = "inventory-topic-retry-1";
        final var expectedRetry2Topic = "inventory-topic-retry-2";
        final var expectedDltTopic = "inventory-topic-retry-dlt";

        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkusEvent
                .from(List.of(aInventory.getSku()));
        final var aOutboxEvent = OutboxEventEntity.from(aInventoryCreatedRollbackEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(5);

        Mockito.doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("Error on remove inventory by sku use case");
        }).when(removeInventoryBySkuUseCase).execute(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(inventoryEventListener).onDltMessage(Mockito.any(), Mockito.any(), Mockito.any());

        producer().send(new ProducerRecord<>(inventoryTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        Mockito.verify(inventoryEventListener, Mockito.times(expectedMaxAttempts)).onMessage(eq(aMessage), Mockito.any(), metadata.capture());

        final var allMetas = metadata.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        Mockito.verify(inventoryEventListener, Mockito.times(expectedMaxDltAttempts)).onDltMessage(eq(aMessage), Mockito.any(), metadata.capture());

        Assertions.assertEquals(expectedDltTopic, metadata.getValue().topic());
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkusEventButOccurredOnIsAfter10Days_whenReceive_shouldNotProcessIt() throws Exception {
        // given
        final var aMockMetadata = Mockito.mock(ConsumerRecordMetadata.class);
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkusEvent
                .from(List.of(aInventory.getSku()));
        final var aOutboxEvent = OutboxEventEntity.from(aInventoryCreatedRollbackEvent);
        aOutboxEvent.setOccurredOn(aOutboxEvent.getOccurredOn()
                .minus(11, ChronoUnit.DAYS));

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.when(aMockMetadata.topic()).thenReturn(inventoryTopic);
        Mockito.when(aMockMetadata.partition()).thenReturn(1);
        Mockito.when(aMockMetadata.offset()).thenReturn(1L);
        Mockito.when(aMockMetadata.timestampType()).thenReturn(TimestampType.CREATE_TIME);
        Mockito.when(aMockMetadata.timestamp()).thenReturn(1L);
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aInventoryListener = new InventoryEventListener(removeInventoryBySkuUseCase, kafkaTemplate);
        aInventoryListener.onMessage(aMessage, aMockAcknowledgment, aMockMetadata);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(0))
                .execute(Mockito.any());
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkusEvent_whenReceive_shouldRemoveInventories() throws Exception {
        // given
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkusEvent
                .from(List.of(aInventory.getSku()));
        final var aOutboxEvent = OutboxEventEntity.from(aInventoryCreatedRollbackEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(removeInventoryBySkuUseCase).execute(Mockito.any());

        // when
        producer().send(new ProducerRecord<>(inventoryTopic, aMessage));
        producer().flush();

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        // then
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(1))
                .execute(Mockito.any());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() {
        // given
        final var aOutboxEntity = OutboxEventEntity.from(new
                TestListenerDomainEvent(InventoryID.unique().getValue()));
        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEntity, aOutboxEntity, aSource(), Operation.CREATE)));

        // when
        producer().send(new ProducerRecord<>(inventoryTopic, aMessage));
        producer().flush();

        // then
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(0))
                .execute(Mockito.any());
    }

    @Test
    void givenAValidInventoryCreatedRollbackBySkusEvent_whenReceiveOnDLTAndSendToPrimaryTopic_shouldResendToPrimaryTopic() throws Exception {
        // given
        final var mockMetadata = Mockito.mock(ConsumerRecordMetadata.class);
        final var mockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aInventoryCreatedRollbackEvent = InventoryCreatedRollbackBySkusEvent
                .from(List.of(aInventory.getSku()));
        final var aOutboxEvent = OutboxEventEntity.from(aInventoryCreatedRollbackEvent);

        final var aMessage = Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(aOutboxEvent, aOutboxEvent, aSource(), Operation.CREATE)));

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.when(mockMetadata.topic()).thenReturn(inventoryTopic);
        Mockito.when(mockMetadata.partition()).thenReturn(1);
        Mockito.when(mockMetadata.offset()).thenReturn(1L);
        Mockito.when(mockMetadata.timestampType()).thenReturn(TimestampType.CREATE_TIME);
        Mockito.when(mockMetadata.timestamp()).thenReturn(1L);
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(mockAcknowledgment).acknowledge();

        final var aInventoryEventListener = new InventoryEventListener(removeInventoryBySkuUseCase, kafkaTemplate);
        aInventoryEventListener.onDltMessage(aMessage, mockAcknowledgment, mockMetadata);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(0))
                .execute(Mockito.any());
    }
}
