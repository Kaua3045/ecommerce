package com.kaua.ecommerce.infrastructure.inventory;

import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InventoryMySQLGateway implements InventoryGateway {

    private static final Logger log = LoggerFactory.getLogger(InventoryMySQLGateway.class);

    private final InventoryJpaEntityRepository inventoryJpaEntityRepository;

    public InventoryMySQLGateway(final InventoryJpaEntityRepository inventoryJpaEntityRepository) {
        this.inventoryJpaEntityRepository = Objects.requireNonNull(inventoryJpaEntityRepository);
    }

    @Transactional
    @Override
    public Set<Inventory> createInBatch(Set<Inventory> inventories) {
        final var aResult = this.inventoryJpaEntityRepository.saveAll(inventories.stream()
                .map(InventoryJpaEntity::toEntity)
                .toList());

        log.info("inserted inventories: {}", aResult.stream().map(InventoryJpaEntity::getSku).toList());
        return inventories;
    }

    @Override
    public List<String> existsBySkus(List<String> skus) {
        return this.inventoryJpaEntityRepository.existsBySkus(skus);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Inventory> findBySku(String sku) {
        return this.inventoryJpaEntityRepository.findBySku(sku)
                .map(InventoryJpaEntity::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Inventory> findByProductId(String productId) {
        return this.inventoryJpaEntityRepository.findByProductId(productId)
                .stream()
                .map(InventoryJpaEntity::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public void cleanByProductId(String productId) {
        this.inventoryJpaEntityRepository.deleteAllByProductId(productId);
        log.info("deleted inventories by productId: {}", productId);
    }

    @Override
    public void deleteBySku(String sku) {
        this.inventoryJpaEntityRepository.deleteBySku(sku);
        log.info("deleted inventory by sku: {}", sku);
    }
}
