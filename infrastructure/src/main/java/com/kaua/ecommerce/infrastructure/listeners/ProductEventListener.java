package com.kaua.ecommerce.infrastructure.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.usecases.product.search.remove.RemoveProductUseCase;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.listeners.models.MessageValue;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProductEventListener {

    public static final TypeReference<MessageValue<OutboxEventEntity>> PRODUCT_MESSAGE = new TypeReference<>() {
    };
    private static final Logger LOG = LoggerFactory.getLogger(ProductEventListener.class);

    private static final String NOT_FOUND_MESSAGE = "Product not found in database: {}";
    private static final String EVENT_RECEIVED_MESSAGE = "Product {} received from Kafka: {}";

    private final ProductGateway productGateway;
    private final SaveProductUseCase saveProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;

    public ProductEventListener(
            final ProductGateway productGateway,
            final SaveProductUseCase saveProductUseCase,
            final RemoveProductUseCase removeProductUseCase
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.saveProductUseCase = Objects.requireNonNull(saveProductUseCase);
        this.removeProductUseCase = Objects.requireNonNull(removeProductUseCase);
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
    public void onMessage(@Payload final String payload, final Acknowledgment ack) {
        LOG.debug("Message received from Kafka: {}", payload);
        final var aOutBoxEvent = Json.readValue(payload, PRODUCT_MESSAGE).payload().after();

        switch (aOutBoxEvent.getEventType()) {
            case EventsTypes.PRODUCT_CREATED -> {
                final var aProductCreated = Json.readValue(aOutBoxEvent.getData(), ProductCreatedEvent.class);
                final var aProductId = aProductCreated.id();
                this.productGateway.findById(aProductId)
                        .ifPresentOrElse(aProduct -> {
                            this.saveProductUseCase.execute(aProduct);
                            ack.acknowledge();
                            LOG.debug(EVENT_RECEIVED_MESSAGE, "created", aProductId);
                        }, () -> LOG.debug(NOT_FOUND_MESSAGE, aProductId));
            }
            case EventsTypes.PRODUCT_UPDATED -> {
                final var aProductUpdated = Json.readValue(aOutBoxEvent.getData(), ProductUpdatedEvent.class);
                final var aProductId = aProductUpdated.id();
                this.productGateway.findById(aProductId)
                        .ifPresentOrElse(aProduct -> {
                            this.saveProductUseCase.execute(aProduct);
                            ack.acknowledge();
                            LOG.debug(EVENT_RECEIVED_MESSAGE, "updated", aProductId);
                        }, () -> LOG.debug(NOT_FOUND_MESSAGE, aProductId));
            }
            case EventsTypes.PRODUCT_DELETED -> {
                final var aProductUpdated = Json.readValue(aOutBoxEvent.getData(), ProductDeletedEvent.class);
                final var aProductId = aProductUpdated.id();
                this.removeProductUseCase.execute(aProductId);
                ack.acknowledge();
                LOG.debug(EVENT_RECEIVED_MESSAGE, "deleted", aProductId);
            }
            default -> LOG.warn("Event type not supported: {}", aOutBoxEvent.getEventType());
        }
    }
}