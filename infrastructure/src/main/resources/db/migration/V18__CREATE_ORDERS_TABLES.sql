CREATE TABLE orders_deliveries (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    freight_type VARCHAR(50) NOT NULL,
    freight_price DECIMAL(19,2) NOT NULL,
    delivery_estimated integer NOT NULL,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(50) NOT NULL,
    complement VARCHAR(255),
    district VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(50) NOT NULL
);

CREATE TABLE orders_payments (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    payment_method_id VARCHAR(36) NOT NULL,
    installments integer NOT NULL
);

CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    order_code VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    total_price DECIMAL(19,2) NOT NULL,
    coupon_code VARCHAR(255),
    coupon_percentage DECIMAL(5,2),
    order_delivery_id VARCHAR(36) NOT NULL,
    order_payment_id VARCHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    FOREIGN KEY (order_delivery_id) REFERENCES orders_deliveries(id) ON DELETE CASCADE,
    FOREIGN KEY (order_payment_id) REFERENCES orders_payments(id) ON DELETE CASCADE
);

CREATE TABLE orders_items (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    quantity integer NOT NULL,
    price DECIMAL(19,2) NOT NULL
);

CREATE SEQUENCE order_code_sequence AS BIGINT START WITH 0 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
