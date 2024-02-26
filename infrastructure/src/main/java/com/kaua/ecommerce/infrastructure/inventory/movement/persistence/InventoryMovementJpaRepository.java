package com.kaua.ecommerce.infrastructure.inventory.movement.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementJpaEntity, String> {
}
