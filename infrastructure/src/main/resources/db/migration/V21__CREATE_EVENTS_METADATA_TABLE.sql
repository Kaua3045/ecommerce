CREATE TABLE events_metadata (
    id VARCHAR(36) NOT NULL,
    payload_identifier VARCHAR(36) NOT NULL,
    version BIGINT NOT NULL,
    occurred_on DATETIME(6) NOT NULL,
    consumed_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (id, consumed_by),
    CONSTRAINT idx_payload_identifier_version_consumed_by UNIQUE (payload_identifier, version, consumed_by)
);