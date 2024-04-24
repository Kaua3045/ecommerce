package com.kaua.ecommerce.infrastructure.event;

public interface EventMetadataGateway {

    void save(final EventMetadataJpaEntity eventMetadataJpaEntity);

    boolean existsByEventId(final String eventId, final EventMetadataConsumer consumedBy);

    boolean newerEventProcessed(final String payloadId, final long version, final EventMetadataConsumer consumedBy);

    void deleteByEventId(final String eventId, final EventMetadataConsumer consumedBy);
}
