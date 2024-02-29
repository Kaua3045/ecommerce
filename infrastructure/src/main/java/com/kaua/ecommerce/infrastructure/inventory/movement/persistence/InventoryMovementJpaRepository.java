package com.kaua.ecommerce.infrastructure.inventory.movement.persistence;

import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementJpaEntity, String> {

    Optional<InventoryMovementJpaEntity> findFirstBySkuAndMovementTypeOrderByCreatedAtDesc(String sku, InventoryMovementStatus status);

}
