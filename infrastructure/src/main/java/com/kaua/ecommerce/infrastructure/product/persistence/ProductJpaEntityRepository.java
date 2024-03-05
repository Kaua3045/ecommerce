package com.kaua.ecommerce.infrastructure.product.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaEntityRepository extends JpaRepository<ProductJpaEntity, String> {
}
