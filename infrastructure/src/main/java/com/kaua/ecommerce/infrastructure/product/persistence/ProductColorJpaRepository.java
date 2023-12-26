package com.kaua.ecommerce.infrastructure.product.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductColorJpaRepository extends JpaRepository<ProductColorJpaEntity, String> {

    Optional<ProductColorJpaEntity> findByColorIgnoreCase(String color);
}
