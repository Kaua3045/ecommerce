package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;

import java.util.Optional;
import java.util.Set;

public interface InventoryMovementGateway {

    Set<InventoryMovement> createInBatch(Set<InventoryMovement> inventoryMovement);

    InventoryMovement create(InventoryMovement inventoryMovement);

    Optional<InventoryMovement> findBySkuAndCreatedAtDescAndStatusRemoved(String sku);

    void deleteById(String id);
}
