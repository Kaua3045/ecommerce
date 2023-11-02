CREATE TABLE addresses (
    address_id VARCHAR(36) PRIMARY KEY NOT NULL,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(255) NOT NULL,
    complement VARCHAR(255),
    district VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    zip_code VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

ALTER TABLE customers ADD COLUMN address_id VARCHAR(36);
ALTER TABLE customers ADD CONSTRAINT fk_address_id FOREIGN KEY (address_id) REFERENCES addresses(address_id);