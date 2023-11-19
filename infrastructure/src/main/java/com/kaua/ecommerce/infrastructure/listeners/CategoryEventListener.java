package com.kaua.ecommerce.infrastructure.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaua.ecommerce.domain.category.CategoryCreatedEvent;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.infrastructure.category.CategoryElasticsearchGateway;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CategoryEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryEventListener.class);

    private final CategoryElasticsearchGateway categoryElasticsearchGateway;

    public CategoryEventListener(final CategoryElasticsearchGateway categoryElasticsearchGateway) {
        this.categoryElasticsearchGateway = Objects.requireNonNull(categoryElasticsearchGateway);
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
    public void onMessage(@Payload final ConsumerRecord<?, ?> payload) throws JsonProcessingException {
        // TODO: refactor this
        LOG.debug("Message received from Kafka: {}", payload);
        LOG.debug("Message received from Kafka: {}", payload.value());

        final var aResult = payload.value();

        if (aResult instanceof DomainEvent aDomainEvent) {
            if (aDomainEvent.eventType().equals(EventsTypes.CATEGORY_CREATED)) {
                final var aCategoryEvent = (CategoryCreatedEvent) aResult;
                final var aCategory = aCategoryEvent.toDomain();
                LOG.debug("Category received from Kafka: {}", aCategory.getName());
                this.categoryElasticsearchGateway.save(aCategory);
            }
        }
    }
}
