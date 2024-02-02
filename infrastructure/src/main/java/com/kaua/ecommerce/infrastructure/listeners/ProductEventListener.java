package com.kaua.ecommerce.infrastructure.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
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

    private final ProductGateway productGateway;
    private final SaveProductUseCase saveProductUseCase;

    public ProductEventListener(
            final ProductGateway productGateway,
            final SaveProductUseCase saveProductUseCase
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.saveProductUseCase = Objects.requireNonNull(saveProductUseCase);
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
                            LOG.debug("Product created received from Kafka: {}", aProductId);
                        }, () -> LOG.debug("Product created not found in database: {}", aProductId));
            }
            default -> LOG.warn("Event type not supported: {}", aOutBoxEvent.getEventType());
        }
    }
}
