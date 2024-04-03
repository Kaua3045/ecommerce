package com.kaua.ecommerce.infrastructure.job;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventStatus;
import com.kaua.ecommerce.infrastructure.service.EventProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class PublishOutboxEventsJob {

    private static final Logger log = LoggerFactory.getLogger(PublishOutboxEventsJob.class);

    private final EventProducerService eventProducerService;
    private final OutboxEventRepository outboxEventRepository;
    private final TransactionManager transactionManager;

    public PublishOutboxEventsJob(
            final EventProducerService eventProducerService,
            final OutboxEventRepository outboxEventRepository,
            final TransactionManager transactionManager
    ) {
        this.eventProducerService = Objects.requireNonNull(eventProducerService);
        this.outboxEventRepository = Objects.requireNonNull(outboxEventRepository);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Scheduled(
            fixedRateString = "${ecommerce.outbox.publish-rate-minutes}",
            initialDelayString = "${ecommerce.outbox.publish-initial-delay-minutes}",
            timeUnit = TimeUnit.MINUTES
    )
    public void publish() {
        log.info("Publishing outbox events");

        boolean pending = true;
        while (pending) {
            final var aResult = this.transactionManager.execute(() -> {
                final var aOutboxEvents = this.outboxEventRepository
                        .findTop50ByStatusOrderByOccurredOnAsc(OutboxEventStatus.PENDING);

                if (aOutboxEvents.isEmpty()) {
                    return false;
                }

                aOutboxEvents.forEach(aOutboxEvent -> {
                    try {
                        final var aTopic = aOutboxEvent.getAggregateName().equalsIgnoreCase("product")
                                ? aOutboxEvent.getEventType().toLowerCase() + "-topic"
                                : aOutboxEvent.getAggregateName().toLowerCase() + "-topic";

                        this.eventProducerService.send(aOutboxEvent, aTopic);
                        aOutboxEvent.setStatus(OutboxEventStatus.PROCESSED);
                        this.outboxEventRepository.save(aOutboxEvent);
                        log.info("Outbox event published: {}", aOutboxEvent);
                    } catch (final RuntimeException ex) {
                        log.error("Error publishing outbox event: {}", aOutboxEvent, ex);
                        aOutboxEvent.setStatus(OutboxEventStatus.FAILED);
                        this.outboxEventRepository.save(aOutboxEvent);
                    }
                });

                return true;
            });

            if (aResult.isFailure()) {
                log.error("Error on execute publish outbox events jobs with error: {}", aResult.getErrorResult().message());
                pending = false;
            } else {
                pending = aResult.getSuccessResult();
            }
        }
    }
}
