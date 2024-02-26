package com.kaua.ecommerce.infrastructure.inventory.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, String> {

    @Query(value = "SELECT i.sku FROM InventoryJpaEntity i WHERE i.sku IN :skus")
    List<String> existsBySkus(List<String> skus);

    Optional<InventoryJpaEntity> findBySku(String sku);

    boolean existsByProductId(String productId);

    void deleteAllByProductId(String productId);

    void deleteBySku(String sku);
}
