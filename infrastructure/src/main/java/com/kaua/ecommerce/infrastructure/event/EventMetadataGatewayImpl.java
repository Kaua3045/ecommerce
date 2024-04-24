package com.kaua.ecommerce.infrastructure.event;

import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class EventMetadataGatewayImpl implements EventMetadataGateway {

    private static final String EVENT_METADATA_PREFIX = "event-metadata:";

    private final EventMetadataJpaEntityRepository eventMetadataJpaEntityRepository;
    private final RedisTemplate<String, Number> redisTemplate;

    public EventMetadataGatewayImpl(
            final EventMetadataJpaEntityRepository eventMetadataJpaEntityRepository,
            final RedisTemplate<String, Number> redisTemplate
    ) {
        this.eventMetadataJpaEntityRepository = Objects.requireNonNull(eventMetadataJpaEntityRepository);
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
    }

    @Override
    public void save(EventMetadataJpaEntity eventMetadataJpaEntity) {
        this.eventMetadataJpaEntityRepository.save(eventMetadataJpaEntity);
    }

    @Override
    public boolean existsByEventId(String eventId, EventMetadataConsumer consumedBy) {
        final var aKey = EVENT_METADATA_PREFIX
                .concat("eventId:")
                .concat(eventId)
                .concat(":consumedBy:")
                .concat(consumedBy.name());
        final var aExistsInCache = this.redisTemplate.opsForValue().get(aKey);

        if (aExistsInCache != null) {
            return true;
        }

        return this.eventMetadataJpaEntityRepository.findByEventMetadataIdEventIdAndEventMetadataIdConsumedBy(
                eventId, consumedBy
        ).map(eventMetadataJpaEntity -> {
            this.redisTemplate.opsForValue().set(
                    aKey,
                    eventMetadataJpaEntity.getVersion(),
                    1L,
                    TimeUnit.HOURS
            );
            return true;
        }).orElse(false);
    }

    @Override
    public boolean newerEventProcessed(String payloadIdentifier, long version, EventMetadataConsumer consumedBy) {
        // Podemos salvar outro aqui no redis mas também precisa ser atomico
        // também devemos colocar um TTL para o cache
        return this.eventMetadataJpaEntityRepository
                .existsByPayloadIdentifierAndConsumedByAndVersionGreaterThan(payloadIdentifier, consumedBy, version);
    }

    @Transactional
    @Override
    public void deleteByEventId(String eventId, EventMetadataConsumer consumedBy) {
        this.eventMetadataJpaEntityRepository.deleteByEventMetadataIdEventIdAndEventMetadataIdConsumedBy(
                eventId, consumedBy
        );
        final var aKey = EVENT_METADATA_PREFIX
                .concat("eventId:")
                .concat(eventId)
                .concat(":consumedBy:")
                .concat(consumedBy.name());
        this.redisTemplate.delete(aKey);
    }
}
