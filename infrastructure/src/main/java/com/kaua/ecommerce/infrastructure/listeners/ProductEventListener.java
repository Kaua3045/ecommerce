package com.kaua.ecommerce.infrastructure.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.usecases.product.search.remove.RemoveProductUseCase;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.listeners.models.MessageValue;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import com.kaua.ecommerce.infrastructure.service.EventValidationService;
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

import java.util.Objects;

@Component
public class ProductEventListener {

    public static final TypeReference<MessageValue<OutboxEventEntity>> PRODUCT_MESSAGE = new TypeReference<>() {
    };
    private static final Logger LOG = LoggerFactory.getLogger(ProductEventListener.class);

    private static final String NOT_FOUND_MESSAGE = "Product not found in database: {}";
    private static final String EVENT_RECEIVED_MESSAGE = "Product {} received from Kafka: {}";
    private static final String PRODUCT_TOPIC = "product-topic";
    private static final String PRODUCT_DLT_INVALID = "product-dlt-invalid";

    private final ProductGateway productGateway;
    private final SaveProductUseCase saveProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventValidationService eventValidationService;

    public ProductEventListener(
            final ProductGateway productGateway,
            final SaveProductUseCase saveProductUseCase,
            final RemoveProductUseCase removeProductUseCase,
            final KafkaTemplate<String, Object> kafkaTemplate,
            final EventValidationService eventValidationService
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.saveProductUseCase = Objects.requireNonNull(saveProductUseCase);
        this.removeProductUseCase = Objects.requireNonNull(removeProductUseCase);
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
        this.eventValidationService = Objects.requireNonNull(eventValidationService);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.products.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = "${kafka.consumers.products.topics}",
            groupId = "${kafka.consumers.products.group-id}",
            id = "${kafka.consumers.products.id}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.products.auto-offset-reset}"
            }
    )
    @RetryableTopic(
            // backoff delay 2 seconds, multiplier 2
            backoff = @Backoff(delay = 2000, multiplier = 2),
            attempts = "${kafka.consumers.products.max-attempts}",
            autoCreateTopics = "${kafka.consumers.products.auto-create-topics}",
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

        final var aOutBoxEvent = Json.readValue(payload, PRODUCT_MESSAGE).payload().after();

        switch (aOutBoxEvent.getEventType()) {
            case EventsTypes.PRODUCT_CREATED -> {
                final var aProductCreated = Json.readValue(aOutBoxEvent.getData(), ProductCreatedEvent.class);
                final var aProductId = aProductCreated.id();

                processCreatedAndUpdatedEvent(
                        aProductId,
                        ack,
                        "created",
                        aProductCreated,
                        payload
                );
            }
            case EventsTypes.PRODUCT_UPDATED -> {
                final var aProductUpdated = Json.readValue(aOutBoxEvent.getData(), ProductUpdatedEvent.class);
                final var aProductId = aProductUpdated.id();

                processCreatedAndUpdatedEvent(
                        aProductId,
                        ack,
                        "updated",
                        aProductUpdated,
                        payload
                );
            }
            case EventsTypes.PRODUCT_DELETED -> {
                final var aProductUpdated = Json.readValue(aOutBoxEvent.getData(), ProductDeletedEvent.class);
                final var aProductId = aProductUpdated.id();

                if (this.eventValidationService.isInvalid(aProductUpdated, aProductId)) {
                    this.kafkaTemplate.send(PRODUCT_DLT_INVALID, payload);
                    LOG.error("Product event is old, sent to DLT: {} and now: {}, payload: {}",
                            aProductUpdated.occurredOn(), InstantUtils.now(), payload);
                    ack.acknowledge();
                    return;
                }

                this.removeProductUseCase.execute(aProductId);
                ack.acknowledge();
                this.eventValidationService.invalidate(aProductUpdated.aggregateName(), aProductId);
                LOG.info(EVENT_RECEIVED_MESSAGE, "deleted", aProductId);
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

        final var aOutBoxEvent = Json.readValue(payload, PRODUCT_MESSAGE).payload().after();
        final var aInstantNow = InstantUtils.now();

        this.kafkaTemplate.send(PRODUCT_TOPIC, payload);
        acknowledgment.acknowledge();
        LOG.warn("Event sent to product topic for retry: {}, occurred on: {} and now: {}, payload: {}",
                PRODUCT_TOPIC, aOutBoxEvent.getOccurredOn(), aInstantNow, payload);
    }

    private <T extends DomainEvent> void processCreatedAndUpdatedEvent(
            final String aId,
            final Acknowledgment ack,
            final String type,
            final T aDomainEvent,
            final String aPayload
    ) {
        if (this.eventValidationService.isInvalid(aDomainEvent, aId)) {
            this.kafkaTemplate.send(PRODUCT_DLT_INVALID, aPayload);
            ack.acknowledge();
            LOG.error("Product event is old, sent to DLT: {} and now: {}, payload: {}",
                    aDomainEvent.occurredOn(), InstantUtils.now(), aPayload);
            return;
        }

        this.productGateway.findById(aId)
                .ifPresentOrElse(aProduct -> {
                    this.saveProductUseCase.execute(aProduct);
                    ack.acknowledge();
                    LOG.info(EVENT_RECEIVED_MESSAGE, type, aId);
                }, () -> LOG.debug(NOT_FOUND_MESSAGE, aId));
    }
}
