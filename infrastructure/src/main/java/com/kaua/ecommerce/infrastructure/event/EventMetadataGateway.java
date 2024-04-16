package com.kaua.ecommerce.infrastructure.event;

public interface EventMetadataGateway {

    void save(final EventMetadataJpaEntity eventMetadataJpaEntity);

    boolean existsByEventId(final String eventId);

    boolean newerEventProcessed(final String payloadId, final long version);

    void deleteByEventId(final String eventId);
}
