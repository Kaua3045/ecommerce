package com.kaua.ecommerce.infrastructure.inventory.movement;

import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
public class InventoryMovementMySQLGateway implements InventoryMovementGateway {

    private static final Logger log = LoggerFactory.getLogger(InventoryMovementMySQLGateway.class);

    private final InventoryMovementJpaRepository inventoryMovementJpaRepository;

    public InventoryMovementMySQLGateway(final InventoryMovementJpaRepository inventoryMovementJpaRepository) {
        this.inventoryMovementJpaRepository = Objects.requireNonNull(inventoryMovementJpaRepository);
    }

    @Override
    public Set<InventoryMovement> createInBatch(Set<InventoryMovement> inventoryMovement) {
        final var aResult = this.inventoryMovementJpaRepository.saveAll(inventoryMovement.stream()
                .map(InventoryMovementJpaEntity::toEntity)
                .toList());

        log.info("inserted inventory movements: {}", aResult.stream().map(InventoryMovementJpaEntity::getSku).toList());
        return inventoryMovement;
    }
}
