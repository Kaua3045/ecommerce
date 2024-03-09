package com.kaua.ecommerce.infrastructure.inventory.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryJpaEntityRepository extends JpaRepository<InventoryJpaEntity, String> {

    @Query(value = "SELECT i.sku FROM InventoryJpaEntity i WHERE i.sku IN :skus")
    List<String> existsBySkus(List<String> skus);

    Optional<InventoryJpaEntity> findBySku(String sku);

    List<InventoryJpaEntity> findByProductId(String productId);

    Page<InventoryJpaEntity> findAll(Specification<InventoryJpaEntity> whereClause, Pageable pageable);

    void deleteAllByProductId(String productId);

    void deleteBySku(String sku);
}
