CREATE TABLE coupons (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    percentage DECIMAL(5,2) NOT NULL,
    expiration_date DATETIME(6) NOT NULL,
    is_active BOOLEAN NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT NOT NULL
);

CREATE TABLE coupons_slots (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    coupon_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE
);