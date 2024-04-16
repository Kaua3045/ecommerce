package com.kaua.ecommerce.infrastructure.event;

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
    public boolean existsByEventId(String eventId) {
        final var aKey = EVENT_METADATA_PREFIX + "eventId:" + eventId;
        final var aExistsInCache = this.redisTemplate.opsForValue().get(aKey);

        if (aExistsInCache != null) {
            return true;
        }

        return this.eventMetadataJpaEntityRepository.findById(eventId)
                .map(eventMetadataJpaEntity -> {
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
    public boolean newerEventProcessed(String payloadIdentifier, long version) {
        // Podemos salvar outro aqui no redis mas também precisa ser atomico
        // também devemos colocar um TTL para o cache
        return this.eventMetadataJpaEntityRepository.existsByPayloadIdentifierAndVersionGreaterThan(payloadIdentifier, version);
    }

    @Override
    public void deleteByEventId(String eventId) {
        this.eventMetadataJpaEntityRepository.deleteById(eventId);
        this.redisTemplate.delete(EVENT_METADATA_PREFIX + "eventId:" + eventId);
    }
}
