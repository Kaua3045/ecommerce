CREATE TABLE categories (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255),
    slug VARCHAR(255) UNIQUE NOT NULL,
    parent_id VARCHAR(36),
    sub_categories_level INT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE categories_relations (
    parent_id VARCHAR(255) NOT NULL,
    sub_category_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (parent_id, sub_category_id),
    FOREIGN KEY (parent_id) REFERENCES categories (id),
    FOREIGN KEY (sub_category_id) REFERENCES categories (id)
);