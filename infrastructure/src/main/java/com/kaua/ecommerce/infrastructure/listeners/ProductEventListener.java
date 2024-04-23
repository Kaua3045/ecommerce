package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.usecases.product.search.remove.RemoveProductUseCase;
import com.kaua.ecommerce.application.usecases.product.search.save.SaveProductUseCase;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.infrastructure.backpressure.BackpressureFactory;
import com.kaua.ecommerce.infrastructure.backpressure.BackpressureStrategies;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.event.EventMetadataGateway;
import com.kaua.ecommerce.infrastructure.event.EventMetadataJpaEntity;
import com.kaua.ecommerce.infrastructure.exceptions.KafkaHeadersException;
import com.kaua.ecommerce.infrastructure.exceptions.NewerEventProcessedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class ProductEventListener extends EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProductEventListener.class);

    private static final String NOT_FOUND_MESSAGE = "Product not found in database: {}";
    private static final String EVENT_RECEIVED_MESSAGE = "Product {} received from Kafka: {}";
    private static final String PRODUCT_TOPIC = "product-topic";
    private static final String PRODUCT_DLT_INVALID = "product-dlt-invalid";

    private final ProductGateway productGateway;
    private final SaveProductUseCase saveProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final EventMetadataGateway eventMetadataGateway;
    private final BackpressureFactory backpressureFactory;

    public ProductEventListener(
            final ProductGateway productGateway,
            final SaveProductUseCase saveProductUseCase,
            final RemoveProductUseCase removeProductUseCase,
            final KafkaTemplate<String, Object> kafkaTemplate,
            final EventMetadataGateway eventMetadataGateway,
            final BackpressureFactory backpressureFactory
    ) {
        super(kafkaTemplate);
        this.productGateway = Objects.requireNonNull(productGateway);
        this.saveProductUseCase = Objects.requireNonNull(saveProductUseCase);
        this.removeProductUseCase = Objects.requireNonNull(removeProductUseCase);
        this.eventMetadataGateway = Objects.requireNonNull(eventMetadataGateway);
        this.backpressureFactory = Objects.requireNonNull(backpressureFactory);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.products.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = {
                    "${kafka.consumers.products.topics.[0]}",
                    "${kafka.consumers.products.topics.[1]}",
                    "${kafka.consumers.products.topics.[2]}",
                    "${kafka.consumers.products.topics.[3]}"
            },
            groupId = "${kafka.consumers.products.group-id}",
            // generate a random id for the consumer
            id = "${kafka.consumers.products.id}-#{T(java.util.UUID).randomUUID().toString()}",
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
            @Payload final ConsumerRecord<String, String> record,
            final Acknowledgment ack
    ) {
        LOG.atLevel(Level.INFO).log("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        try {
            if (this.backpressureFactory.shouldWait(BackpressureStrategies.SQL_ELASTICSEARCH_CACHE)) {
                LOG.info("Backpressure applied, waiting to process messages");
                return;
            }

            final var aEventId = getEventIdHeaderValue(record);
            final var eventType = getEventTypeHeaderValue(record);
            final var aOccurredOn = getOccurredOnHeaderValue(record);

            if (this.eventMetadataGateway.existsByEventId(aEventId)) {
                LOG.warn("Event {} occurredOn: {} already processed eventId: {}, payload: {}", eventType, aOccurredOn, aEventId, record.value());
                ack.acknowledge();
                return;
            }

            final var payload = record.value();

            switch (eventType) {
                case EventsTypes.PRODUCT_CREATED -> {
                    final var aProductCreated = Json.readValue(payload, ProductCreatedEvent.class);
                    final var aProductId = aProductCreated.id();

                    processCreatedAndUpdatedEvent(
                            aProductId,
                            ack,
                            "created",
                            aProductCreated,
                            record
                    );
                }
                case EventsTypes.PRODUCT_UPDATED -> {
                    final var aProductUpdated = Json.readValue(payload, ProductUpdatedEvent.class);
                    final var aProductId = aProductUpdated.id();

                    processCreatedAndUpdatedEvent(
                            aProductId,
                            ack,
                            "updated",
                            aProductUpdated,
                            record
                    );
                }
                case EventsTypes.PRODUCT_DELETED -> {
                    final var aProductDeleted = Json.readValue(payload, ProductDeletedEvent.class);
                    final var aProductId = aProductDeleted.id();

                    if (this.eventMetadataGateway.newerEventProcessed(aProductId, aProductDeleted.version())) {
                        handleMessagingErrorAndSendToDlt(
                                record,
                                PRODUCT_DLT_INVALID,
                                ack,
                                LOG,
                                NewerEventProcessedException.getExceptionMessage(aProductId)
                        );
                        return;
                    }

                    this.removeProductUseCase.execute(aProductId);
                    saveEventMetadata(aProductDeleted, aProductId);
                    ack.acknowledge();
                    LOG.info(EVENT_RECEIVED_MESSAGE, "deleted", aProductId);
                }
                default -> handleMessagingTypeNotSupported(
                        record,
                        eventType,
                        PRODUCT_DLT_INVALID,
                        ack,
                        LOG
                );
            }
        } catch (final KafkaHeadersException ex) {
            handleMessagingNotContainsHeaders(
                    record,
                    PRODUCT_DLT_INVALID,
                    ack,
                    LOG,
                    ex.getMessage()
            );
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

        // Usamos o getEventType pois aqui usamos product_event_type O event type seria por ex product_created
        // então seria mandado pro topico product_created, se não tivermos mais de 1 topico por entity
        // então não precisamos disso
        final var aTopicRetry = new String(record.headers().lastHeader("kafka_original-topic").value())
                .equalsIgnoreCase(PRODUCT_TOPIC)
                ? PRODUCT_TOPIC
                : getEventTypeHeaderValue(record) + "-topic";

        handlePublishMessageToRetryTopic(
                record,
                aTopicRetry,
                acknowledgment,
                LOG
        );
    }

    private <T extends DomainEvent> void processCreatedAndUpdatedEvent(
            final String aId,
            final Acknowledgment ack,
            final String type,
            final T aDomainEvent,
            final ConsumerRecord<String, String> message
    ) {
        if (this.eventMetadataGateway.newerEventProcessed(aId, aDomainEvent.version())) {
            handleMessagingErrorAndSendToDlt(
                    message,
                    PRODUCT_DLT_INVALID,
                    ack,
                    LOG,
                    NewerEventProcessedException.getExceptionMessage(aId)
            );
            return;
        }

        this.productGateway.findById(aId)
                .ifPresentOrElse(aProduct -> {
                    this.saveProductUseCase.execute(aProduct);
                    // TODO: Need attention here, save event possible to throws unique constraint violation
                    //  because the payload identifier and versions is the same
                    saveEventMetadata(aDomainEvent, aId);
                    ack.acknowledge();
                    LOG.info(EVENT_RECEIVED_MESSAGE, type, aId);
                }, () -> {
                    LOG.debug(NOT_FOUND_MESSAGE, aId);
                    this.eventMetadataGateway.deleteByEventId(getEventIdHeaderValue(message));
                    ack.nack(Duration.ofSeconds(5));
                });
    }

    private void saveEventMetadata(final DomainEvent aDomainEvent, final String aPayloadId) {
        this.eventMetadataGateway.save(EventMetadataJpaEntity.from(
                aDomainEvent,
                aPayloadId
        ));
    }
}
