package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.inventory.events.InventoryCreatedRollbackBySkuEvent;
import com.kaua.ecommerce.infrastructure.backpressure.BackpressureFactory;
import com.kaua.ecommerce.infrastructure.backpressure.BackpressureStrategies;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.event.EventMetadataGateway;
import com.kaua.ecommerce.infrastructure.event.EventMetadataJpaEntity;
import com.kaua.ecommerce.infrastructure.exceptions.KafkaHeadersException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InventoryEventListener extends EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryEventListener.class);

    private static final String EVENT_RECEIVED_MESSAGE = "Inventory {} received from Kafka: {}";
    private static final String INVENTORY_TOPIC = "inventory-topic";
    private static final String INVENTORY_DLT_INVALID = "inventory-dlt-invalid";

    private final RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;
    private final EventMetadataGateway eventMetadataGateway;
    private final BackpressureFactory backpressureFactory;

    public InventoryEventListener(
            final RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase,
            final KafkaTemplate<String, Object> kafkaTemplate,
            final EventMetadataGateway eventMetadataGateway,
            final BackpressureFactory backpressureFactory
    ) {
        super(kafkaTemplate);
        this.removeInventoryBySkuUseCase = Objects.requireNonNull(removeInventoryBySkuUseCase);
        this.eventMetadataGateway = Objects.requireNonNull(eventMetadataGateway);
        this.backpressureFactory = Objects.requireNonNull(backpressureFactory);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.products.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = "${kafka.consumers.inventories.topics}",
            groupId = "${kafka.consumers.inventories.group-id}",
            id = "${kafka.consumers.inventories.id}-#{T(java.util.UUID).randomUUID().toString()}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.inventories.auto-offset-reset}"
            }
    )
    @RetryableTopic(
            backoff = @Backoff(delay = 2000, multiplier = 2),
            attempts = "${kafka.consumers.inventories.max-attempts}",
            autoCreateTopics = "${kafka.consumers.inventories.auto-create-topics}",
            dltTopicSuffix = "-retry-dlt",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void onMessage(
            @Payload final ConsumerRecord<String, String> record,
            final Acknowledgment ack
    ) {
        LOG.atLevel(Level.INFO).log("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        try {
            if (this.backpressureFactory.shouldWait(BackpressureStrategies.SQL_CACHE)) {
                LOG.info("Backpressure applied, waiting to process messages");
                return;
            }

            final var aEventId = getEventIdHeaderValue(record);
            final var aEventType = getEventTypeHeaderValue(record);
            final var aOccurredOn = getOccurredOnHeaderValue(record);

            if (this.eventMetadataGateway.existsByEventId(aEventId)) {
                LOG.warn("Event {} occurredOn: {} already processed eventId: {}, payload: {}", aEventType, aOccurredOn, aEventId, record.value());
                ack.acknowledge();
                return;
            }

            final var payload = record.value();

            switch (aEventType) {
                case EventsTypes.INVENTORY_CREATED_ROLLBACK_BY_SKU -> {
                    final var aInventoryCreatedRollbackBySku = Json.readValue(
                            payload, InventoryCreatedRollbackBySkuEvent.class);

                    this.eventMetadataGateway.save(EventMetadataJpaEntity.from(
                            aInventoryCreatedRollbackBySku,
                            aInventoryCreatedRollbackBySku.sku()
                    ));
                    this.removeInventoryBySkuUseCase.execute(aInventoryCreatedRollbackBySku.sku());

                    LOG.info(EVENT_RECEIVED_MESSAGE, "created rollback by sku", aInventoryCreatedRollbackBySku);
                    ack.acknowledge();
                }
                default -> handleMessagingTypeNotSupported(
                        record,
                        aEventType,
                        INVENTORY_DLT_INVALID,
                        ack,
                        LOG
                );
            }
        } catch (final KafkaHeadersException e) {
            handleMessagingNotContainsHeaders(
                    record,
                    INVENTORY_DLT_INVALID,
                    ack,
                    LOG,
                    e.getMessage()
            );
        } catch (final DataIntegrityViolationException ex) {
            handleMessagingErrorAndSendToDlt(
                    record,
                    INVENTORY_DLT_INVALID,
                    ack,
                    LOG,
                    ex.getMessage());
        } catch (final Exception ex) {
            this.eventMetadataGateway.deleteByEventId(getEventIdHeaderValue(record));
            throw ex;
        }
    }

    @DltHandler
    public void onDltMessage(
            @Payload ConsumerRecord<String, String> record,
            final Acknowledgment acknowledgment
    ) {
        LOG.atLevel(Level.WARN).log("Message received from Kafka at DLT [topic:{}] [partition:{}] [offset:{}]: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        handlePublishMessageToRetryTopic(record, INVENTORY_TOPIC, acknowledgment, LOG);
    }
}
