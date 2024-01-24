CREATE TABLE products_images (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL
);

CREATE TABLE products (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(19, 2) NOT NULL,
    quantity integer NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    banner_image_id VARCHAR(36),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE products_colors (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    color VARCHAR(255) NOT NULL
);

CREATE TABLE products_sizes (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    size_name VARCHAR(10) NOT NULL,
    weight double NOT NULL,
    height double NOT NULL,
    width double NOT NULL,
    depth double NOT NULL
);

CREATE TABLE products_attributes (
    product_id VARCHAR(36) NOT NULL,
    color_id VARCHAR(36) NOT NULL,
    size_id VARCHAR(36) NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (color_id) REFERENCES products_colors(id),
    FOREIGN KEY (size_id) REFERENCES products_sizes(id)
);


CREATE TABLE products_images_relations (
    product_id VARCHAR(36) NOT NULL,
    image_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (image_id) REFERENCES products_images(id)
);