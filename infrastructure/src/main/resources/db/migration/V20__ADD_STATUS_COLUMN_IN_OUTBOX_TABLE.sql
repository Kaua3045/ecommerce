ALTER TABLE outbox ADD COLUMN status varchar(30) NOT NULL;
CREATE INDEX idx_status ON outbox (status);