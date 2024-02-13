DROP TABLE addresses;

ALTER TABLE customers DROP FOREIGN KEY fk_address_id;
ALTER TABLE customers DROP COLUMN address_id;