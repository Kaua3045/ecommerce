package com.kaua.ecommerce.infrastructure.inventory;

import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.utils.SpecificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    public Inventory update(Inventory inventory) {
        final var aResult = this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(inventory))
                .toDomain();
        log.info("updated inventory: {}", aResult);
        return aResult;
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
    public Pagination<Inventory> findAllByProductId(SearchQuery aQuery, String productId) {
        final var aPage = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var aSpecificationLike = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecificationLike)
                .orElse(null);

        final var aSpecification = Specification.where(assembleWhereEqual(productId))
                .and(aSpecificationLike);

        final var aPageResult = this.inventoryJpaEntityRepository.findAll(
                aSpecification,
                aPage
        );

        return new Pagination<>(
                aPageResult.getNumber(),
                aPageResult.getSize(),
                aPageResult.getTotalPages(),
                aPageResult.getTotalElements(),
                aPageResult.map(InventoryJpaEntity::toDomain).toList()
        );
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

    private Specification<InventoryJpaEntity> assembleSpecificationLike(final String terms) {
        return SpecificationUtils.like("sku", terms);
    }

    private Specification<InventoryJpaEntity> assembleWhereEqual(final String aProductId) {
        return SpecificationUtils.equal("productId", aProductId);
    }
}
