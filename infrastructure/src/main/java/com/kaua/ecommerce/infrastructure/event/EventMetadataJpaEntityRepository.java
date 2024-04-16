package com.kaua.ecommerce.infrastructure.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMetadataJpaEntityRepository extends JpaRepository<EventMetadataJpaEntity, String> {

    boolean existsByPayloadIdentifierAndVersionGreaterThan(String payloadIdentifier, long version);
}
