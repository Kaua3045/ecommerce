CREATE TABLE inventories (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    quantity integer NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE INDEX idx_inventories_product_id ON inventories (product_id);