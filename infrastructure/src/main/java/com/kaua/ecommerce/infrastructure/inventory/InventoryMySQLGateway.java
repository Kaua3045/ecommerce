package com.kaua.ecommerce.infrastructure.inventory;

import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class InventoryMySQLGateway implements InventoryGateway {

    private static final Logger log = LoggerFactory.getLogger(InventoryMySQLGateway.class);

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryMySQLGateway(final InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = Objects.requireNonNull(inventoryJpaRepository);
    }

    @Transactional
    @Override
    public Set<Inventory> createInBatch(Set<Inventory> inventories) {
        final var aResult = this.inventoryJpaRepository.saveAll(inventories.stream()
                .map(InventoryJpaEntity::toEntity)
                .toList());

        log.info("inserted inventories: {}", aResult.size());
        return inventories;
    }

    @Override
    public List<String> existsBySkus(List<String> skus) {
        return this.inventoryJpaRepository.existsBySkus(skus);
    }

    @Transactional
    @Override
    public void cleanByProductId(String productId) {
        if (this.inventoryJpaRepository.existsByProductId(productId)) {
            this.inventoryJpaRepository.deleteAllByProductId(productId);
            log.info("deleted inventories by productId: {}", productId);
        }
    }
}
