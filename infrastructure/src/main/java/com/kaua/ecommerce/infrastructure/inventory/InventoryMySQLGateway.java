package com.kaua.ecommerce.infrastructure.inventory;

import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class InventoryMySQLGateway implements InventoryGateway {

    private static final Logger log = LoggerFactory.getLogger(InventoryMySQLGateway.class);

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryMySQLGateway(final InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = Objects.requireNonNull(inventoryJpaRepository);
    }

    @Transactional
    @Override
    public Inventory create(final Inventory inventory) {
        final var aResult = this.inventoryJpaRepository.save(InventoryJpaEntity.toEntity(inventory)).toDomain();
        log.info("inserted inventory: {}", aResult);
        return aResult;
    }

    @Override
    public boolean existsBySku(final String sku) {
        return this.inventoryJpaRepository.existsBySku(sku);
    }
}
