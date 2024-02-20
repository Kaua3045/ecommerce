package com.kaua.ecommerce.infrastructure.inventory.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, String> {

    boolean existsBySku(String sku);
}
