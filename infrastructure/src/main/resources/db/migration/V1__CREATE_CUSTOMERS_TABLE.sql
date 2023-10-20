CREATE TABLE customers (
    customer_id VARCHAR(36) PRIMARY KEY NOT NULL,
    account_id VARCHAR(36) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);