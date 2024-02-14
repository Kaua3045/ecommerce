CREATE TABLE outbox (
    event_id VARCHAR(36) PRIMARY KEY NOT NULL,
    aggregate_name VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    data JSON NOT NULL,
    occurred_on DATETIME(6) NOT NULL
);