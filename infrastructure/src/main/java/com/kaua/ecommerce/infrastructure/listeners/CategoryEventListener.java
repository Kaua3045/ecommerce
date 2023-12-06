package com.kaua.ecommerce.infrastructure.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kaua.ecommerce.application.usecases.category.search.remove.DefaultRemoveCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.search.remove.RemoveCategoryCommand;
import com.kaua.ecommerce.application.usecases.category.search.save.DefaultSaveCategoryUseCase;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryDeletedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryUpdatedEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
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
public class CategoryEventListener {

    public static final TypeReference<MessageValue<OutboxEventEntity>> CATEGORY_MESSAGE = new TypeReference<>() {
    };
    private static final Logger LOG = LoggerFactory.getLogger(CategoryEventListener.class);

    private final DefaultSaveCategoryUseCase saveCategoryUseCase;
    private final DefaultRemoveCategoryUseCase removeCategoryUseCase;

    public CategoryEventListener(
            final DefaultSaveCategoryUseCase saveCategoryUseCase,
            final DefaultRemoveCategoryUseCase removeCategoryUseCase
    ) {
        this.saveCategoryUseCase = Objects.requireNonNull(saveCategoryUseCase);
        this.removeCategoryUseCase = Objects.requireNonNull(removeCategoryUseCase);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.categories.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = "${kafka.consumers.categories.topics}",
            groupId = "${kafka.consumers.categories.group-id}",
            id = "${kafka.consumers.categories.id}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.categories.auto-offset-reset}"
            }
    )
    public void onMessage(@Payload final String payload, final Acknowledgment ack) {
        LOG.debug("Message received from Kafka: {}", payload);
        final var aOutBoxEvent = Json.readValue(payload, CATEGORY_MESSAGE).payload().after();

        switch (aOutBoxEvent.getEventType()) {
            case EventsTypes.CATEGORY_CREATED -> {
                final var aCategoryCreated = Json.readValue(aOutBoxEvent.getData(), CategoryCreatedEvent.class);
                final var aCategory = aCategoryCreated.toDomain();
                this.saveCategoryUseCase.execute(aCategory);
                ack.acknowledge();
                LOG.debug("Category created received from Kafka: {}", aCategory.getName());
            }
            case EventsTypes.CATEGORY_UPDATED -> {
                final var aCategoryUpdated = Json.readValue(aOutBoxEvent.getData(), CategoryUpdatedEvent.class);
                final var aCategory = aCategoryUpdated.toDomain();
                this.saveCategoryUseCase.execute(aCategory);
                ack.acknowledge();
                LOG.debug("Category updated received from Kafka: {}", aCategory.getName());
            }
            case EventsTypes.CATEGORY_DELETED -> {
                final var aCategoryDeleted = Json.readValue(aOutBoxEvent.getData(), CategoryDeletedEvent.class);
                this.removeCategoryUseCase.execute(RemoveCategoryCommand
                        .with(aCategoryDeleted.rootCategoryId(), aCategoryDeleted.subCategoryId()
                                .orElse(null)));
                ack.acknowledge();
                LOG.debug("Category deleted received from Kafka: {}", aOutBoxEvent.getData());
            }
            default -> LOG.warn("Event type not supported: {}", aOutBoxEvent.getEventType());
        }
    }
}
