package com.kaua.ecommerce.infrastructure.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventMetadataJpaEntityRepository extends JpaRepository<EventMetadataJpaEntity, EventMetadataJpaEntityId> {

    Optional<EventMetadataJpaEntity> findByEventMetadataIdEventIdAndEventMetadataIdConsumedBy(String eventId, EventMetadataConsumer consumedBy);

    @Query("SELECT COUNT(e) > 0 FROM EventMetadataJpaEntity e WHERE e.payloadIdentifier = :payloadIdentifier AND e.eventMetadataId.consumedBy = :consumedBy AND e.version > :version")
    boolean existsByPayloadIdentifierAndConsumedByAndVersionGreaterThan(String payloadIdentifier, EventMetadataConsumer consumedBy, long version);

    void deleteByEventMetadataIdEventIdAndEventMetadataIdConsumedBy(String eventId, EventMetadataConsumer consumedBy);
}
