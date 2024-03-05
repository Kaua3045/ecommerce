package com.kaua.ecommerce.infrastructure.category.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface CategoryJpaEntityRepository extends JpaRepository<CategoryJpaEntity, String> {

    boolean existsByName(String name);

    @Override
    @NonNull
    @Query("SELECT c FROM CategoryJpaEntity c LEFT JOIN FETCH c.subCategories WHERE c.id = :id")
    Optional<CategoryJpaEntity> findById(@NonNull String id);
}
