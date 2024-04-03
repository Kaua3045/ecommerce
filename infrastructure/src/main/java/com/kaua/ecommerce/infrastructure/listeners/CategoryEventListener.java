package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.category.search.remove.DefaultRemoveCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.search.remove.RemoveCategoryCommand;
import com.kaua.ecommerce.application.usecases.category.search.save.DefaultSaveCategoryUseCase;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryDeletedEvent;
import com.kaua.ecommerce.domain.category.events.CategoryUpdatedEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CategoryEventListener {

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
    public void onMessage(@Payload final ConsumerRecord<String, String> record, final Acknowledgment ack) {
        LOG.debug("Message received from Kafka: {}", record.value());

        final var aEventType = new String(record.headers().lastHeader("event_type").value());

        switch (aEventType) {
            case EventsTypes.CATEGORY_CREATED -> {
                final var aCategoryCreated = Json.readValue(record.value(), CategoryCreatedEvent.class);
                final var aCategory = aCategoryCreated.toDomain();
                this.saveCategoryUseCase.execute(aCategory);
                ack.acknowledge();
                LOG.debug("Category created received from Kafka: {}", aCategory.getName());
            }
            case EventsTypes.CATEGORY_UPDATED -> {
                final var aCategoryUpdated = Json.readValue(record.value(), CategoryUpdatedEvent.class);
                final var aCategory = aCategoryUpdated.toDomain();
                this.saveCategoryUseCase.execute(aCategory);
                ack.acknowledge();
                LOG.debug("Category updated received from Kafka: {}", aCategory.getName());
            }
            case EventsTypes.CATEGORY_DELETED -> {
                final var aCategoryDeleted = Json.readValue(record.value(), CategoryDeletedEvent.class);
                this.removeCategoryUseCase.execute(RemoveCategoryCommand
                        .with(aCategoryDeleted.rootCategoryId(), aCategoryDeleted.subCategoryId()
                                .orElse(null)));
                ack.acknowledge();
                LOG.debug("Category deleted received from Kafka: {}", record.value());
            }
            default -> LOG.warn("Event type not supported: {}", aEventType);
        }
    }
}
