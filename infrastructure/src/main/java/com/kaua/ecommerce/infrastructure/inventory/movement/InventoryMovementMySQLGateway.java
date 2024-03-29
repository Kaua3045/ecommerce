package com.kaua.ecommerce.infrastructure.inventory.movement;

import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class InventoryMovementMySQLGateway implements InventoryMovementGateway {

    private static final Logger log = LoggerFactory.getLogger(InventoryMovementMySQLGateway.class);

    private final InventoryMovementJpaEntityRepository inventoryMovementJpaEntityRepository;

    public InventoryMovementMySQLGateway(final InventoryMovementJpaEntityRepository inventoryMovementJpaEntityRepository) {
        this.inventoryMovementJpaEntityRepository = Objects.requireNonNull(inventoryMovementJpaEntityRepository);
    }

    @Override
    public Set<InventoryMovement> createInBatch(Set<InventoryMovement> inventoryMovement) {
        final var aResult = this.inventoryMovementJpaEntityRepository.saveAll(inventoryMovement.stream()
                .map(InventoryMovementJpaEntity::toEntity)
                .toList());

        log.info("inserted inventory movements: {}", aResult.stream().map(InventoryMovementJpaEntity::getSku).toList());
        return inventoryMovement;
    }

    @Override
    public InventoryMovement create(InventoryMovement inventoryMovement) {
        final var aResult = this.inventoryMovementJpaEntityRepository
                .save(InventoryMovementJpaEntity.toEntity(inventoryMovement)).toDomain();

        log.info("inserted inventory movement: {}", aResult);
        return aResult;
    }

    @Override
    public Optional<InventoryMovement> findBySkuAndCreatedAtDescAndStatusRemoved(String sku) {
        return this.inventoryMovementJpaEntityRepository.findFirstBySkuAndMovementTypeOrderByCreatedAtDesc(
                sku, InventoryMovementStatus.REMOVED
        ).map(InventoryMovementJpaEntity::toDomain);
    }

    @Override
    public void deleteById(String id) {
        this.inventoryMovementJpaEntityRepository.deleteById(id);
        log.info("deleted inventory movement with id: {}", id);
    }
}
