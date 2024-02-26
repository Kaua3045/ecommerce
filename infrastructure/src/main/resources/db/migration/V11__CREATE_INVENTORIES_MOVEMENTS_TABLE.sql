CREATE TABLE inventories_movements (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    inventory_id VARCHAR(36) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    quantity integer NOT NULL,
    movement_type VARCHAR(100) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE INDEX idx_inventories_sku ON inventories_movements (sku);