package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryMySQLGateway(final CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = Objects.requireNonNull(categoryJpaRepository);
    }

    @Override
    public Category create(Category aCategory) {
        return this.categoryJpaRepository.save(CategoryJpaEntity.toEntity(aCategory)).toDomain();
    }

    @Override
    public boolean existsByName(String aName) {
        return this.categoryJpaRepository.existsByName(aName);
    }
}
