CREATE TABLE events_metadata (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    payload_identifier VARCHAR(36) NOT NULL,
    version BIGINT NOT NULL,
    occurred_on DATETIME(6) NOT NULL,
    CONSTRAINT idx_payload_identifier_version UNIQUE (payload_identifier, version)
);