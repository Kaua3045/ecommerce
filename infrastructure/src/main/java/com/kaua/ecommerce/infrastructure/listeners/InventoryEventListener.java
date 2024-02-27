package com.kaua.ecommerce.infrastructure.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.inventory.events.InventoryCreatedRollbackBySkusEvent;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.listeners.models.MessageValue;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class InventoryEventListener {

    public static final TypeReference<MessageValue<OutboxEventEntity>> INVENTORY_MESSAGE = new TypeReference<>() {
    };
    private static final Logger LOG = LoggerFactory.getLogger(InventoryEventListener.class);

    private static final String EVENT_RECEIVED_MESSAGE = "Inventory {} received from Kafka: {}";
    private static final String INVENTORY_TOPIC = "inventory-topic";
    private static final String INVENTORY_DLT_INVALID = "inventory-dlt-invalid";

    private final RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventListener(
            final RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase,
            final KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.removeInventoryBySkuUseCase = removeInventoryBySkuUseCase;
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.products.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = "${kafka.consumers.inventories.topics}",
            groupId = "${kafka.consumers.inventories.group-id}",
            id = "${kafka.consumers.inventories.id}",
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
            @Payload final String payload,
            final Acknowledgment ack,
            final ConsumerRecordMetadata metadata
    ) {
        LOG.atLevel(Level.INFO).log("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: {}",
                metadata.topic(), metadata.partition(), metadata.offset(), payload);

        final var aOutBoxEvent = Json.readValue(payload, INVENTORY_MESSAGE).payload().after();
        final var aInstantNow = InstantUtils.now();
        final var aDuration = Duration.between(aOutBoxEvent.getOccurredOn(), aInstantNow);

        if (aDuration.toDays() > 10) {
            this.kafkaTemplate.send(INVENTORY_DLT_INVALID, payload);
            ack.acknowledge();
            LOG.error("Event sent to DLT topic: {}, occurred on: {} and now: {}, payload: {}",
                    INVENTORY_DLT_INVALID, aOutBoxEvent.getOccurredOn(), aInstantNow, payload);
            return;
        }

        switch (aOutBoxEvent.getEventType()) {
            case EventsTypes.INVENTORY_CREATED_ROLLBACK_BY_SKUS -> {
                final var aInventoryCreatedRollbackBySkus = Json.readValue(
                        aOutBoxEvent.getData(), InventoryCreatedRollbackBySkusEvent.class);

                final var aSkus = aInventoryCreatedRollbackBySkus.skus();
                aSkus.forEach(this.removeInventoryBySkuUseCase::execute);

                LOG.info(EVENT_RECEIVED_MESSAGE, "created rollback by skus", aInventoryCreatedRollbackBySkus);

                ack.acknowledge();
            }
            default -> LOG.warn("Event type not supported: {}", aOutBoxEvent.getEventType());
        }
    }

    @DltHandler
    public void onDltMessage(
            @Payload String payload,
            final Acknowledgment acknowledgment,
            final ConsumerRecordMetadata metadata
    ) {
        LOG.atLevel(Level.WARN).log("Message received from Kafka at DLT [topic:{}] [partition:{}] [offset:{}]: {}",
                metadata.topic(), metadata.partition(), metadata.offset(), payload);

        final var aOutBoxEvent = Json.readValue(payload, INVENTORY_MESSAGE).payload().after();
        final var aInstantNow = InstantUtils.now();

        this.kafkaTemplate.send(INVENTORY_TOPIC, payload);
        acknowledgment.acknowledge();
        LOG.warn("Event sent to inventory topic for retry: {}, occurred on: {} and now: {}, payload: {}",
                INVENTORY_TOPIC, aOutBoxEvent.getOccurredOn(), aInstantNow, payload);
    }
}
